package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.UserIdNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results, false);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet, true);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    @Override
    public BigDecimal getBalance(int id) throws UserIdNotFoundException {
        String sql = "SELECT balance FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()) {
            return results.getBigDecimal("balance");
        }
        throw new UserIdNotFoundException(id + "is not a valid user id.");
    }

    // Creates a new transfer and updates both account balances
    @Override
    public boolean transferTo(Transfer newTransfer) throws UserIdNotFoundException {
        String sqlInsertTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (" + Transfer.TRANSFER_TYPE_SEND + ", " + Transfer.TRANSFER_STATUS_APPROVED + ", " +
                "(SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?)";
        String sqlTransferOut = "UPDATE account SET balance = (SELECT balance - ? FROM account WHERE user_id = ?) WHERE user_id = ?";
        String sqlTransferIn = "UPDATE account SET balance = (SELECT balance + ? FROM account WHERE user_id = ?) WHERE user_id = ?";
        try {
            jdbcTemplate.update(sqlInsertTransfer, newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());
            jdbcTemplate.update(sqlTransferIn, newTransfer.getAmount(), newTransfer.getAccountTo(), newTransfer.getAccountTo());
            jdbcTemplate.update(sqlTransferOut, newTransfer.getAmount(), newTransfer.getAccountFrom(), newTransfer.getAccountFrom());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    // Gets a current list of transfers that checks for the user as either a recipient or sender
    @Override
    public List<Transfer> getHistory(int id) throws UserIdNotFoundException {
        List<Transfer> transferHistory = new ArrayList<>();
        try {
            String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, amount, from_account.account_id AS from_id, \n" +
                    "to_account.account_id AS to_id, to_user.username AS to_name, from_user.username AS from_name\n" +
                    "FROM transfer \n" +
                    "    JOIN account AS from_account ON transfer.account_from = from_account.account_id \n" +
                    "    JOIN tenmo_user AS from_user ON from_user.user_id = from_account.user_id \n" +
                    "    JOIN account AS to_account ON transfer.account_to = to_account.account_id\n" +
                    "    JOIN tenmo_user AS to_user ON to_user.user_id = to_account.user_id\n" +
                    "WHERE from_user.user_id = ? \n" +
                    "UNION \n" +
                    "SELECT transfer_id, transfer_type_id, transfer_status_id, amount, from_account.account_id AS from_id, \n" +
                    "to_account.account_id AS to_id, to_user.username AS to_name, from_user.username AS from_name\n" +
                    "FROM transfer \n" +
                    "    JOIN account AS from_account ON transfer.account_from = from_account.account_id \n" +
                    "    JOIN tenmo_user AS from_user ON from_user.user_id = from_account.user_id \n" +
                    "    JOIN account AS to_account ON transfer.account_to = to_account.account_id\n" +
                    "    JOIN tenmo_user AS to_user ON to_user.user_id = to_account.user_id\n" +
                    "WHERE to_user.user_id = ? \n" +
                    "ORDER BY transfer_id";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);

            while (results.next()) {
                transferHistory.add(mapRowToTransfer(results));
            }
        }
        catch (UserIdNotFoundException | DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return transferHistory;
    }

    // Creates a transfer, but does not adjust any balances
    @Override
    public boolean requestBucks(Transfer newTransfer) {
        String sqlRequestTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (" + Transfer.TRANSFER_TYPE_REQUEST + ", " + Transfer.TRANSFER_STATUS_PENDING + ", " +
                "(SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?)";
        try {
            jdbcTemplate.update(sqlRequestTransfer, newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());
        }
        catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    // Retrieves all transfers with the status of pending
    @Override
    public List<Transfer> getPending(int id) {
        List<Transfer> pendingRequests = new ArrayList<>();
        try {
            String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, amount, from_account.account_id AS from_id, to_account.account_id AS to_id, from_user.username AS from_name, to_user.username AS to_name FROM transfer" +
                    "    JOIN account AS from_account ON transfer.account_from = from_account.account_id \n" +
                    "    JOIN tenmo_user AS from_user ON from_user.user_id = from_account.user_id \n" +
                    "    JOIN account AS to_account ON transfer.account_to = to_account.account_id\n" +
                    "    JOIN tenmo_user AS to_user ON to_user.user_id = to_account.user_id WHERE from_user.user_id = ? AND\n" +
                    "    transfer_type_id = 1 AND transfer_status_id = 1 ORDER BY transfer_id";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while(results.next()) {
                pendingRequests.add(mapRowToTransfer(results));
            }
        }
        catch (UserIdNotFoundException | DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return pendingRequests;
    }

    // Updates the transfer based upon the user's choice
    @Override
    public boolean updatePending(Transfer transfer) {
        String sqlUpdateStatus = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        try {
            if(transfer.getStatusId() == Transfer.TRANSFER_STATUS_REJECTED) {
                jdbcTemplate.update(sqlUpdateStatus, transfer.getStatusId(), transfer.getTransferId());
            }
            else if(transfer.getStatusId() == Transfer.TRANSFER_STATUS_APPROVED) {
                //check for available balance first
                BigDecimal balance = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE account_id = ?", BigDecimal.class, transfer.getAccountFrom());
                if (balance.compareTo(transfer.getAmount()) < 0) { return false; }
                //update the balances
                String sqlTransferOut = "UPDATE account SET balance = (SELECT balance - ? FROM account WHERE account_id = ?) WHERE account_id = ?";
                String sqlTransferIn = "UPDATE account SET balance = (SELECT balance + ? FROM account WHERE account_id = ?) WHERE account_id = ?";
                jdbcTemplate.update(sqlUpdateStatus, transfer.getStatusId(), transfer.getTransferId());
                jdbcTemplate.update(sqlTransferOut, transfer.getAmount(), transfer.getAccountFrom(), transfer.getAccountFrom());
                jdbcTemplate.update(sqlTransferIn, transfer.getAmount(), transfer.getAccountTo(), transfer.getAccountTo());
            }
        }
        catch (UserIdNotFoundException | DataAccessException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }


    private User mapRowToUser(SqlRowSet rs, Boolean includePassword) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setActivated(true);
        user.setAuthorities("USER");
        if (includePassword) { user.setPassword(rs.getString("password_hash")); }
        return user;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTypeId(rs.getInt("transfer_type_id"));
        transfer.setStatusId(rs.getInt("transfer_status_id"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setFromName(rs.getString("from_name"));
        transfer.setToName(rs.getString("to_name"));
        transfer.setAccountFrom(rs.getInt("from_id"));
        transfer.setAccountTo(rs.getInt("to_id"));
        return transfer;
    }
}
