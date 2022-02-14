package com.foxconn.fii.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenInvalidException extends AuthenticationException {
    private static final long serialVersionUID = -294671188037098603L;

    private String token;

    public JwtTokenInvalidException(String msg) {
        super(msg);
    }

    public JwtTokenInvalidException(String token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token;
    }
}
