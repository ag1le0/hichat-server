package com.foxconn.fii.security.config;

public class JwtProperties {
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String BASIC_TOKEN_PREFIX = "Basic ";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 2 * 60 * 60;
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 30 * 24 * 60 * 60;
    public static final long DEV_ACCESS_TOKEN_VALIDITY_SECONDS = 30 * 24 * 60 * 60;

}
