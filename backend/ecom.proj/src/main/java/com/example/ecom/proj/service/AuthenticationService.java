package com.example.ecom.proj.service;

import com.example.ecom.proj.config.JWTService;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.dao.UserRepository;
import com.example.ecom.proj.dto.AuthTokenDto;
import com.example.ecom.proj.dto.UserLoginRequestDto;
import com.example.ecom.proj.dto.UserRegisterRequestDto;
import com.example.ecom.proj.dto.UserRegisterResponesDto;
import com.example.ecom.proj.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
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
                .token(AuthTokenDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    public AuthTokenDto login(UserLoginRequestDto loginRequest) {
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

        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthTokenDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User nor found"));

        if (!jwtService.isTokenValid(refreshToken, user, userEmail)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String accessToken = jwtService.generateAccessToken(user);
        tokenService.revokeUserToken(user.getId());
        System.out.println("test");
        tokenService.saveUserToken(accessToken, user);
        System.out.println("testt");
        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
