package com.example.ecom.proj.service;

import com.example.ecom.proj.dto.AuthTokenDto;
import com.example.ecom.proj.email.AccountVerificationEmailContext;
import com.example.ecom.proj.email.PasswordResetEmailContext;
import com.example.ecom.proj.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.base-url}")
    private String baseUrl;

    public AccountVerificationEmailContext prepareRegistrationEmail(User user, String token) {
        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(user);            // User info (from the request)
        context.setToken(token);            // newly created token
        context.buildVerificationUrl(baseUrl, token);   // URL in email (consist of token and frontend url)
        return context;
    }

    public PasswordResetEmailContext sendPasswordResetEmail(User user, String resetToken) {
        PasswordResetEmailContext context = new PasswordResetEmailContext();
        context.init(user);
        context.setToken(resetToken);            // newly created token
        context.buildVerificationUrl(baseUrl, resetToken);   // URL in email (consist of token and frontend url)
        return context;
    }

}
