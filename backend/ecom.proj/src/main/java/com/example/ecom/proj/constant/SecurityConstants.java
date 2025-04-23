package com.example.ecom.proj.constant;

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
    public static final String REFRESH_TOKEN_URL = "/refresh-token";
    public static final String LOGOUT_URL = "/logout";
    public static final String LOGOUT_FULL_URL = AUTH_BASE + LOGOUT_URL;

    // Security context
    public static final String CONTEXT_USER_ATTR = "currentUser";

}
