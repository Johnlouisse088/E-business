package com.example.ecom.proj.service;

import com.example.ecom.proj.config.JWTService;
import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.dao.UserRepository;
import com.example.ecom.proj.dto.*;
import com.example.ecom.proj.constant.ErrorMessageConstants;
import com.example.ecom.proj.mapper.UserMapper;
import com.example.ecom.proj.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenService tokenService;
    private final UserMapper userMapper;


    public UserRegisterResponesDto register(UserRegisterRequestDto registerRequest) {
        // use mapper instead:
//        User user = User.builder()
//                .email(registerRequest.getEmail())
//                .firstname(registerRequest.getFirstname())
//                .lastname(registerRequest.getLastname())
//                .password(passwordEncoder.encode(registerRequest.getPassword()))    // Create an encoder later!!
//                .role(registerRequest.getRole())
//                .build();
        // mapper: DTO -> Entity
        User user = userMapper.toEntity(registerRequest);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Saving token of the user
        tokenService.saveUserToken(accessToken, savedUser);

        // use the mapper instead
//        return UserRegisterResponesDto.builder()
//                .email(savedUser.getEmail())
//                .firstname(savedUser.getFirstname())
//                .lastname(savedUser.getLastname())
//                .role(savedUser.getRole())
//                .token(AuthTokenDto.builder()
//                        .accessToken(accessToken)
//                        .refreshToken(refreshToken)
//                        .build())
//                .build();

        // mapper: Entity -> DTO
        return userMapper.toDto(savedUser, accessToken, refreshToken);
    }

    public AuthTokenDto login(UserLoginRequestDto loginRequest) {
        authenticationManager.authenticate(                   // Authenticate user credentials
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByEmail(loginRequest.getEmail())    // Get the row user data using the email
                .orElseThrow();

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

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw new RuntimeException(ErrorMessageConstants.MISSING_AUTH_HEADER);
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException(ErrorMessageConstants.USER_NOT_FOUND));

        if (!jwtService.isTokenValid(refreshToken, user, userEmail)) {
            throw new RuntimeException(ErrorMessageConstants.INVALID_REFRESH_TOKEN);
        }

        String accessToken = jwtService.generateAccessToken(user);
        tokenService.revokeUserToken(user.getId());
        tokenService.saveUserToken(accessToken, user);
        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public MessageResponseDto changePassword(UserChangePasswordRequestDto changePasswordRequest, User currentUser) throws IllegalAccessException {
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalAccessException(ErrorMessageConstants.WRONG_PASSWORD);
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IllegalAccessException(ErrorMessageConstants.NOT_SAME_PASSWORD);
        }

        currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(currentUser);

        return MessageResponseDto.builder()
                .message(SecurityConstants.SUCCESSFULLY_CHANGE_PASSWORD)
                .build();
    }
}
