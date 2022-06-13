package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
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
import java.util.List;

public class UserService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        this.baseUrl = url;
    }

    public BigDecimal getBalance(AuthenticatedUser user) {
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

    public List<User> findAll(AuthenticatedUser user) {
       List<User> allUsers = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(user.getToken());
            HttpEntity<AuthenticatedUser> entity = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(baseUrl + "account/getAll?currentUserId=" + user.getUser().getId(), HttpMethod.GET, entity, List.class);
            allUsers = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return allUsers;
    }
}
