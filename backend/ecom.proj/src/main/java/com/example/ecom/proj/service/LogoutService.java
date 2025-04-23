package com.example.ecom.proj.service;

import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.entity.Token;
import com.example.ecom.proj.constant.ErrorMessageConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw new RuntimeException(ErrorMessageConstants.MISSING_AUTH_HEADER);
        }
        jwt = authHeader.substring(7);
        Token token = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (token != null) {
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
            SecurityContextHolder.clearContext();
        }
    }
}
