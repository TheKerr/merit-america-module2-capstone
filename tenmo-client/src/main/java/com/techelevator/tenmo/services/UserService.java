package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser user;

    public AuthenticatedUser getUser() {
        return user;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public UserService(String url) {
        this.baseUrl = url;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "account/" + user.getUser().getId(), HttpMethod.GET, entity, BigDecimal.class);
            balance = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public List<User> findAll() {
       List<User> allUsers = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
            ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "account/getAll", HttpMethod.GET, entity, User[].class);
            allUsers = List.of(response.getBody());
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return allUsers;
    }

    public boolean transfer(Transfer newTransfer) {
        boolean completed = false;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl + "transfer", HttpMethod.POST, entity, Boolean.class);
            completed = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return completed;
    }

    public List<Transfer> getTransferHistory() {
        List<Transfer> transferHistory = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/history?currentUserId=" + user.getUser().getId(), HttpMethod.GET, entity, Transfer[].class);
            transferHistory = List.of(response.getBody());
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferHistory;
    }

    public boolean request(Transfer newTransfer) {
        boolean completed = false;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl + "transfer/request", HttpMethod.POST, entity, Boolean.class);
            completed = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return completed;
    }

    public List<Transfer> getPendingRequests() {
        List<Transfer> pendingRequests = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/pending?currentUserId=" + user.getUser().getId(), HttpMethod.GET, entity, Transfer[].class);
            pendingRequests = List.of(response.getBody());
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return pendingRequests;
    }

    public boolean updatePending(Transfer transfer) {
        boolean completed = false;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl + "transfer/pending", HttpMethod.PUT, entity, Boolean.class);
            completed = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return completed;
    }
}
