package com.example.ecom.proj.constant;

import java.util.Set;

public class SecurityConstants {



    // Token-related
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_HEADER = "Authorization";

    // Custom claim keys (used when generating JWT)
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_PERMISSIONS = "permissions";

    // Token configuration (you may pull these from application.properties too)
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;  // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;  // 7 days

    // Authentication endpoints
    public static final String AUTH_BASE = "/api/v1/auth";
    public static final String LOGIN_URL = "/login";
    public static final String REGISTER_URL = "/register";
    public static final String REGISTER_EMAIL_URL = "/register-email";
    public static final String REFRESH_TOKEN_URL = "/refresh-token";
    public static final String CHANGE_PASSWORD_URL = "/change-password";
    public static final String EMAIL_VERIFICATION_URL = "/email-verification";
    public static final String EMAIL_FORGOT_PASSWORD_URL = "/forgot-password";
    public static final String EMAIL_PASSWORD_RESET_URL = "/reset-password";
    public static final String LOGOUT_URL = "/logout";
    public static final String EMAIL_VERIFICATION_FULL_URL = AUTH_BASE + EMAIL_VERIFICATION_URL;
    public static final String EMAIL_PASSWORD_RESET_FULL_URL = AUTH_BASE + EMAIL_PASSWORD_RESET_URL;
    public static final String LOGOUT_FULL_URL = AUTH_BASE + LOGOUT_URL;

    // Security context
    public static final String CONTEXT_USER_ATTR = "currentUser";
    public static final String CHECK_EMAIL = "Check your email address for the verification";
    public static final String SUCCESSFULLY_CHANGE_PASSWORD = "Password changed successfully";
    public static final String VERIFICATION_SUCCESS = "Verification success";
    public static final String RESET_PASSWORD_LINK = "Reset link has been sent";

    // No need to authenticate
    public static final Set<String> AUTH_WHITELIST = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password"
            // "/api/v1/auth/change-password"   // You need to authenticate the token

    );



}
