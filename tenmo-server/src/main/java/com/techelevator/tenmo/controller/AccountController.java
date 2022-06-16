package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private UserDao userDao;
    public AccountController(UserDao userDao) { this.userDao = userDao; }

    @RequestMapping(value = "/account/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@Valid @PathVariable int id) {
        return this.userDao.getBalance(id);
    }

    @RequestMapping(value = "/account/getAll", method = RequestMethod.GET)
    public List<User> getTenmoUsers(@RequestParam long currentUserId) {
        return this.userDao.findAll();
    }

    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public boolean transfer(@Valid @RequestBody Transfer newTransfer) {
        return this.userDao.transferTo(newTransfer);
    }

    @RequestMapping(value = "transfer/history", method = RequestMethod.GET)
    public List<Transfer> getTransferHistory(@RequestParam int currentUserId) { return this.userDao.getHistory(currentUserId); }
}
