package com.example.ecom.proj.service;

import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.model.Token;
import com.example.ecom.proj.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveUserToken(String accessToken, User user) {
        Token token = Token.builder()
                .token(accessToken)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

//    // revoking latest token
//    public void revokeUserToken(Token latestToken ) {
//        latestToken.setRevoked(true);
//        latestToken.setExpired(true);
//        tokenRepository.save(latestToken);     // It will update the token as long the latestToken already has an ID. (if there's no id, it will create new token)
//    }

    public void revokeUserToken(Integer userId) {
        List<Token> validUserTokens = tokenRepository.findValidUserTokens(userId);  // Get all tokens with revoked and expired are false
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setRevoked(true);
                token.setExpired(true);
            });
        }
        tokenRepository.saveAll(validUserTokens);      // Single DB hit; Improve Performance
    }

    public Token getLatestValidToken(User user) {
        List<Token> tokens = user.getToken();                                     // Get all tokens
        Token latestToken = tokens.stream()                                      // Get the latest token
                .max(Comparator.comparing(Token::getCreatedAt))
                .orElseThrow();
        return latestToken;
    }
}
