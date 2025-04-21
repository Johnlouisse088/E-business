package com.example.ecom.proj.service;

import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.model.Token;
import com.example.ecom.proj.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepo;

    public void saveUserToken(String accessToken, User user) {
        Token token = Token.builder()
                .token(accessToken)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepo.save(token);
    }
}
