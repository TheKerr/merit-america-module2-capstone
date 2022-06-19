package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    NumberFormat currency = NumberFormat.getCurrencyInstance();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        userService.setUser(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        System.out.println("Your current balance is: ");
        BigDecimal balance = userService.getBalance();
        System.out.println(currency.format(balance));
	}

    private void viewTransferList(List<Transfer> transfer) {
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID      From/To      Status     Amount");
        System.out.println("-------------------------------------------");
        for(Transfer transfers : transfer) {
            System.out.print(transfers.getTransferId() + "  \t");
            if(transfers.getFromName().equals(currentUser.getUser().getUsername())) {
                System.out.print("To: " + transfers.getToName() + "  \t " + transfers.getStatusName() + "  \t" + currency.format(transfers.getAmount()) + "\n");
            }
            else System.out.print("From: " + transfers.getFromName() + "  \t " + transfers.getStatusName() + "  \t" + currency.format(transfers.getAmount()) + "\n");
        }
    }

	private void viewTransferHistory() {
        List<Transfer> userTransfers = userService.getTransferHistory();
        int transferId = 0;
        while (true) {
            viewTransferList(userTransfers);
            System.out.println("");
            transferId = consoleService.promptForInt("Please enter the Transfer ID that you would like to see details of (0 to cancel): ");
            System.out.println("");
            if(transferId == 0) {
                break;
            }
            for(Transfer transfer : userTransfers) {
                if(transfer.getTransferId() == transferId) {
                    System.out.println("");
                    System.out.println("---------------------------");
                    System.out.println("Transfer Details");
                    System.out.println("---------------------------");
                    System.out.println("ID: " + transfer.getTransferId());
                    System.out.println("From: " + transfer.getFromName());
                    System.out.println("To: " + transfer.getToName());
                    System.out.println("Type: " + transfer.getTypeName());
                    System.out.println("Status: " + transfer.getStatusName());
                    System.out.println("Amount: " + currency.format(transfer.getAmount()));
                    consoleService.pause();
                    break;
                }
            }
        }
	}

	private void viewPendingRequests() {
        List<Transfer> pendingTransfers = userService.getPendingRequests();
        int transferId = 0;
        while(true) {
            System.out.println("-----------------------------------");
            System.out.println("Pending Transfers");
            System.out.println("ID      To         Amount");
            System.out.println("-----------------------------------");
            for(Transfer transfer : pendingTransfers) {
                System.out.print(transfer.getTransferId() +" \t" + transfer.getToName()+ "\t \t   " + currency.format(transfer.getAmount()));
                System.out.println("");
            }
            System.out.println("");
            transferId = consoleService.promptForInt("Please enter the transfer ID that you would like to approve/reject (0 to cancel): ");
            if (transferId == 0) {
                break;
            }
            int choice = 0;
            Transfer pendingTransfer = getTransferById(pendingTransfers, transferId);
            if(pendingTransfer != null) {
                System.out.println("1: Approve");
                System.out.println("2: Reject");
                System.out.println("0: Don't approve or reject");
                System.out.println("------------------------------");
                choice = consoleService.promptForInt("Please choose an option: ");
                System.out.println("");
                if(choice > 2 || choice < 0) {
                    System.out.println("Invalid selection, please try again.");
                }
                else {
                    pendingTransfer.setStatusId(choice + 1);
                    boolean success = userService.updatePending(pendingTransfer);
                    if (success) {
                        System.out.println("Transfer has been updated.");
                        break;
                    }
                    else System.out.println("Transfer failed to update.");
                }
            }
            else System.out.println("Invalid transfer selection.");
        }
	}

    private void viewListOfUsers(List<User> tenmoUsers) {
        System.out.println("----------------------");
        System.out.println("Users");
        System.out.println("ID \t \t Name");
        System.out.println("----------------------");
        for (User user: tenmoUsers) {
            System.out.println(user.getId() + "\t" + user.getUsername());
        }
        System.out.println("-----------");
    }

	private void sendBucks() {
        List<User> tenmoUsers = userService.findAll();
        viewListOfUsers(tenmoUsers);
        boolean validSelection = false;
        int transferId = 0;
        while(validSelection == false) {
            transferId = consoleService.promptForInt("Please select the user id you wish to send Bucks to (0 to abort): ");
            if(checkValidId(tenmoUsers, transferId)) {
                validSelection = true;
            }
            if (transferId == 0) {
                return;
            }
        }
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount you wish to transfer: ");
        Transfer newTransfer = new Transfer();
        if(userService.getBalance().compareTo(transferAmount) > -1 && transferAmount.compareTo(BigDecimal.valueOf(0)) > -1) {
            newTransfer.setAccountFrom(currentUser.getUser().getId());
            newTransfer.setAccountTo((long) transferId);
            newTransfer.setStatusId(2);
            newTransfer.setAmount(transferAmount);
            newTransfer.setTypeId(2);
        }
        else {
            System.out.println("Invalid amount. Please enter a valid amount to transfer.");
            return;
        }
        boolean completed = false;
        completed = userService.transfer(newTransfer);
		if (completed == true) {
            System.out.println("Transfer completed.");
        }
        else System.out.println("Transfer has failed.");
	}

    private boolean checkValidId(List<User> tenmoUsers, int id){
        if (id == currentUser.getUser().getId()) {
            System.out.println("Invalid user, please enter a different id.");
            return false;
        }
        for(User users : tenmoUsers) {
            if(id == users.getId()) {
                return true;
            }
        }
        return false;
    }

    private Transfer getTransferById(List<Transfer> transfers, int id){
        for(Transfer transfer : transfers) {
            if(id == transfer.getTransferId()) {
                return transfer;
            }
        }
        return null;
    }

    private boolean checkValidTransferId(List<Transfer> transfers, int id){
        for(Transfer transfer : transfers) {
            if(id == transfer.getTransferId()) {
                return true;
            }
        }
        return false;
    }

	private void requestBucks() {
        List<User> tenmoUsers = userService.findAll();
        viewListOfUsers(tenmoUsers);
        boolean validSelection = false;
        int transferId = 0;
        while(validSelection == false) {
            transferId = consoleService.promptForInt("Enter the ID of the user you want to request from (0 to cancel): ");
            if(transferId == 0) {
                return;
            }
            if(checkValidId(tenmoUsers, transferId)) {
                validSelection = true;
            }
        }
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount you wish to request: ");
        Transfer newTransfer = new Transfer();
        if(transferAmount.compareTo(BigDecimal.valueOf(0)) > -1) {
            newTransfer.setAccountFrom((long) transferId);
            newTransfer.setAccountTo(currentUser.getUser().getId());
            newTransfer.setStatusId(Transfer.TRANSFER_STATUS_PENDING);
            newTransfer.setAmount(transferAmount);
            newTransfer.setTypeId(Transfer.TRANSFER_TYPE_REQUEST);
        }
        else {
            System.out.println("Invalid amount. Please enter a valid amount to request.");
            return;
        }
        boolean completed = false;
        completed = userService.request(newTransfer);
        if (completed == true) {
            System.out.println("Request has been sent.");
        }
        else System.out.println("Request failed to send.");
	}

}
