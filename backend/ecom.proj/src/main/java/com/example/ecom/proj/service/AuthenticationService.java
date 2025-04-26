package com.example.ecom.proj.service;

import com.example.ecom.proj.config.JWTService;
import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.dao.UserRepository;
import com.example.ecom.proj.dto.*;
import com.example.ecom.proj.constant.ErrorMessageConstants;
import com.example.ecom.proj.email.AccountVerificationEmailContext;
import com.example.ecom.proj.email.EmailServiceImpl;
import com.example.ecom.proj.email.PasswordResetEmailContext;
import com.example.ecom.proj.entity.Token;
import com.example.ecom.proj.mapper.UserMapper;
import com.example.ecom.proj.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
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

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final EmailServiceImpl emailServiceImpl;
    private final ObjectMapper objectMapper;


    public UserRegisterResponesDto register(UserRegisterRequestDto registerRequest) {

        // Ensure the email is unique
        ensureUserDoesNotExist(registerRequest);

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

    public MessageResponseDto registerEmailValidation(UserRegisterRequestDto registerRequest) throws MessagingException {
        ensureUserDoesNotExist(registerRequest);

        User user = userMapper.toEntity(registerRequest);
        User savedUser = userRepository.save(user);  // save in database

        // Create token
        String accessToken = jwtService.generateAccessToken(savedUser);

        // Preparing for the email information (like: to, from, subject, email body)
        AccountVerificationEmailContext context = emailService.prepareRegistrationEmail(savedUser, accessToken);
        // Send the email base to the email information we set earlier
        emailServiceImpl.sendMail(context);
        // After clicking the link (After receiving the email you will get the link), you will redirect to the frontend url
        // link consist of token (not already store in database)

        return MessageResponseDto.builder()
                .message(SecurityConstants.CHECK_EMAIL)
                .build();
    }

    public AuthTokenDto login(UserLoginRequestDto loginRequest) {
        authenticationManager.authenticate(                   // Authenticate user credentials
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = findUser(loginRequest.getEmail()); // Get the row user data using the email

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

        User user = findUser(userEmail);

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

    public User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(ErrorMessageConstants.USER_NOT_FOUND));
    }

    public void ensureUserDoesNotExist(UserRegisterRequestDto request) {
        boolean isUserPresent = userRepository.findByEmail(request.getEmail()).isPresent();
        if (isUserPresent) {
            throw new RuntimeException(ErrorMessageConstants.USER_ALREADY_EXISTS);
        }
    }

    public MessageResponseDto verifyUser(String token){
        final String userEmail;

        userEmail = jwtService.extractUsername(token);

        User user = findUser(userEmail);

        tokenService.revokeUserToken(user.getId());
        // Save the token, from the verification link
        tokenService.saveUserToken(token, user);

        return MessageResponseDto.builder()
                .message(SecurityConstants.VERIFICATION_SUCCESS)
                .build();
    }

    public MessageResponseDto forgotPassword(UserForgotPasswordRequestDto forgotPasswordRequest) throws MessagingException {
        User user = findUser(forgotPasswordRequest.getEmail());
        tokenService.revokeUserToken(user.getId());
        String token = jwtService.generateAccessToken(user);
        tokenService.saveUserToken(token, user);

        PasswordResetEmailContext context = emailService.sendPasswordResetEmail(user, token);

        emailServiceImpl.sendMail(context);

        return MessageResponseDto.builder()
                .message(SecurityConstants.RESET_PASSWORD_LINK)
                .build();
    }

    public UserRegisterResponesDto resetPassword(String accessToken, UserResetPasswordDto resetPasswordRequest) {
        final String userEmail;

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new RuntimeException(ErrorMessageConstants.NOT_SAME_PASSWORD);
        }

        userEmail = jwtService.extractUsername(accessToken);
        User user = findUser(userEmail);
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        User savedUser = userRepository.save(user);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return userMapper.toDto(savedUser, accessToken, refreshToken);
    }
}
