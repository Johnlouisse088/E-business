package com.example.ecom.proj.service;

import com.example.ecom.proj.config.JWTService;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.dao.UserRepository;
import com.example.ecom.proj.dto.UserLoginRequestDto;
import com.example.ecom.proj.dto.UserLoginResponseDto;
import com.example.ecom.proj.dto.UserRegisterRequestDto;
import com.example.ecom.proj.dto.UserRegisterResponesDto;
import com.example.ecom.proj.model.Token;
import com.example.ecom.proj.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public UserRegisterResponesDto register(UserRegisterRequestDto registerRequest) {
        User user = User.builder()
                .email(registerRequest.getEmail())
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .password(passwordEncoder.encode(registerRequest.getPassword()))    // Create an encoder later!!
                .role(registerRequest.getRole())
                .build();
        User savedUser = userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Saving token of the user
        tokenService.saveUserToken(accessToken, savedUser);

        return UserRegisterResponesDto.builder()
                .email(registerRequest.getEmail())
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .role(registerRequest.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserLoginResponseDto login(UserLoginRequestDto loginRequest) {
        authenticationManager.authenticate(                   // Authenticate user credentials
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepo.findByEmail(loginRequest.getEmail())    // Get the row user data using the email
                .orElseThrow();

//        // revoke latest token
//        Token latestToken = tokenService.getLatestValidToken(user);     // Get the latest token
//        if (!latestToken.isRevoked() && !latestToken.isExpired()){            // Check if that revoked and expired is false
//            tokenService.revokeUserToken(latestToken);                                   // revoke them, set the revoked and expired to true
//        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenService.revokeUserToken(user.getId());
        tokenService.saveUserToken(accessToken, user);             // save the newly created token in database

        return UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
