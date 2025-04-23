package com.example.ecom.proj.controller;

import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dto.*;
import com.example.ecom.proj.entity.User;
import com.example.ecom.proj.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
