package com.example.ecom.proj.controller;

import com.example.ecom.proj.constant.ErrorMessageConstants;
import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dto.*;
import com.example.ecom.proj.entity.User;
import com.example.ecom.proj.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RestController
@RequestMapping(SecurityConstants.AUTH_BASE)
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @PostMapping(SecurityConstants.REGISTER_URL)
    public ResponseEntity<UserRegisterResponesDto> register(@RequestBody UserRegisterRequestDto registerRequest) {
        return ResponseEntity.ok(service.register(registerRequest));
    }

    // REGISTER_EMAIL_URL -> generate token -> email -> clicked link -> EMAIL_VERIFICATION_URL -> save the token in db
    // 1. Sending a token to an email
    @PostMapping(SecurityConstants.REGISTER_EMAIL_URL)
    public  ResponseEntity<MessageResponseDto> registerEmailValidation(@RequestBody UserRegisterRequestDto registerRequest) throws MessagingException {
        return ResponseEntity.ok(service.registerEmailValidation(registerRequest));
    }

    // 2. After clinking the link (from the email), it will automatically save the token based to the user
    @GetMapping(SecurityConstants.EMAIL_VERIFICATION_URL)
    public ResponseEntity<MessageResponseDto> verifyUser(@RequestParam String token) {
        if (token.isEmpty()) {
            throw new RuntimeException(ErrorMessageConstants.TOKEN_NOT_FOUND);
        }
        try {
            return ResponseEntity.ok(service.verifyUser(token));
        } catch (Error error) {
            throw new RuntimeException(ErrorMessageConstants.USER_NOT_FOUND);
        }
    }

    // Forgot password -> generate token (save in db) -> email -> clicked link -> reset_password -> extracts the token -> set new password
    @PostMapping(SecurityConstants.EMAIL_FORGOT_PASSWORD_URL)
    public ResponseEntity<MessageResponseDto> forgotPassword(@RequestBody UserForgotPasswordRequestDto forgotPasswordRequest) throws MessagingException {
        return ResponseEntity.ok(service.forgotPassword(forgotPasswordRequest));
    }

    @PostMapping(SecurityConstants.EMAIL_PASSWORD_RESET_URL)
    public ResponseEntity<UserRegisterResponesDto> resetPassword(@RequestParam String token,
                                                                 @RequestBody UserResetPasswordDto resetPasswordRequest) {
        return ResponseEntity.ok(service.resetPassword(token, resetPasswordRequest));
    }

    @PostMapping(SecurityConstants.LOGIN_URL)
    public ResponseEntity<AuthTokenDto> login(@RequestBody UserLoginRequestDto loginRequest) {
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @PostMapping(SecurityConstants.REFRESH_TOKEN_URL)
    public ResponseEntity<AuthTokenDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.refreshToken(request, response));
    }

    @PatchMapping(SecurityConstants.CHANGE_PASSWORD_URL)
    public ResponseEntity<MessageResponseDto> changePassword(@RequestBody UserChangePasswordRequestDto changePasswordRequest,
                                                             @AuthenticationPrincipal User currentUser) throws IllegalAccessException {
        return ResponseEntity.ok(service.changePassword(changePasswordRequest, currentUser));
    }




}
