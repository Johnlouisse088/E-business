package com.example.ecom.proj.controller;

import com.example.ecom.proj.dto.UserLoginRequestDto;
import com.example.ecom.proj.dto.UserLoginResponseDto;
import com.example.ecom.proj.dto.UserRegisterRequestDto;
import com.example.ecom.proj.dto.UserRegisterResponesDto;
import com.example.ecom.proj.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponesDto> register(@RequestBody UserRegisterRequestDto registerRequest) {
        return ResponseEntity.ok(service.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto loginRequest) {
        return ResponseEntity.ok(service.login(loginRequest));
    }
}
