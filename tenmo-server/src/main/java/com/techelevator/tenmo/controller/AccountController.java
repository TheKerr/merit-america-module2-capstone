package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private UserDao userDao;
    public AccountController(UserDao userDao) { this.userDao = userDao; }

    @RequestMapping(value = "/{user_id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@Valid @PathVariable int id) {
        return this.userDao.getBalance(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public boolean transfer(@Valid @RequestBody Transfer newTransfer) {
        return this.userDao.transferTo(newTransfer);
    }

}
