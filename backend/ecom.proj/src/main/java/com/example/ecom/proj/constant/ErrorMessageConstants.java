package com.example.ecom.proj.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorMessageConstants {

    // === Authentication Errors ===
    public static final String MISSING_AUTH_HEADER = "Missing or invalid Authorization header";
    public static final String INVALID_CREDENTIALS = "Email or password is incorrect";
    public static final String ACCOUNT_DISABLED = "This account has been disabled";

    // === Token Errors ===
    public static final String TOKEN_EXPIRED = "Your session has expired. Please log in again";
    public static final String TOKEN_INVALID = "The provided token is invalid or has been tampered with";
    public static final String INVALID_REFRESH_TOKEN = "The refresh token is invalid or has expired";
    public static final String TOKEN_NOT_FOUND = "Authentication token not found";
    public static final String TOKEN_ALREADY_REVOKED = "The token has already been revoked";

    // === User Errors ===
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "This email is already registered";
    public static final String USER_UNAUTHORIZED = "You are not authorized to perform this action";

    // === Validation Errors ===
    public static final String INVALID_EMAIL_FORMAT = "Email format is invalid";
    public static final String PASSWORD_TOO_WEAK = "Password must be at least 8 characters long and contain a number";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String NOT_SAME_PASSWORD = "Password are not the same";

    // === Server / Generic Errors ===
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later";
    public static final String RESOURCE_NOT_FOUND = "The requested resource could not be found";
}
