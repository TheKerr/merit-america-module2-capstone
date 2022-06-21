package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private UserDao userDao;
    public AccountController(UserDao userDao) { this.userDao = userDao; }

    @RequestMapping(value = "/account/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@Valid @PathVariable int id, Principal principal) {
        if (id == this.userDao.findByUsername(principal.getName()).getId()) {
            return this.userDao.getBalance(id);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/account/getAll", method = RequestMethod.GET)
    public List<User> getTenmoUsers() {
        return this.userDao.findAll();
    }

    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public boolean transfer(@Valid @RequestBody Transfer newTransfer, Principal principal) {
        if (newTransfer.getFromName().equals(principal.getName())) {
            return this.userDao.transferTo(newTransfer);
        }
        return false;
    }

    @RequestMapping(value = "transfer/history", method = RequestMethod.GET)
    public List<Transfer> getTransferHistory(@RequestParam int currentUserId, Principal principal) {
        if (currentUserId == this.userDao.findByUsername(principal.getName()).getId()) {
            return this.userDao.getHistory(currentUserId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "transfer/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingRequests(@RequestParam int currentUserId, Principal principal) {
        if (currentUserId == this.userDao.findByUsername(principal.getName()).getId()) {
            return this.userDao.getPending(currentUserId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "transfer/request", method = RequestMethod.POST)
    public boolean request(@Valid @RequestBody Transfer newTransfer, Principal principal) {
        if (newTransfer.getToName().equals(principal.getName())) {
            return this.userDao.requestBucks(newTransfer);
        } else {
            return false;
        }
    }

    @RequestMapping(value = "transfer/pending", method = RequestMethod.PUT)
    public boolean updatePending(@Valid @RequestBody Transfer transfer, Principal principal) {
        if (transfer.getFromName().equals(principal.getName())) {
            return this.userDao.updatePending(transfer);
        } else {
            return false;
        }
    }
}
