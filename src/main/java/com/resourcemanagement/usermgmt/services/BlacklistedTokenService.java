package com.resourcemanagement.usermgmt.services;

import com.resourcemanagement.usermgmt.entities.BlacklistedToken;
import com.resourcemanagement.usermgmt.repositories.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlacklistedTokenService {

    @Autowired
    private BlacklistedTokenRepository repository;

    public void blacklistToken(String bearerToken) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(bearerToken);
        repository.save(blacklistedToken);
    }
}