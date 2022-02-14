package com.foxconn.fii.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.common.response.ResponseCode;
import com.foxconn.fii.security.exception.JwtTokenExpiredException;
import com.foxconn.fii.security.exception.JwtTokenInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (e instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), CommonResponse.of(HttpStatus.UNAUTHORIZED, ResponseCode.AUTHENTICATION, "Invalid username or password", null));
        }if (e instanceof JwtTokenInvalidException) {
            mapper.writeValue(response.getWriter(), CommonResponse.of(HttpStatus.UNAUTHORIZED, ResponseCode.AUTHENTICATION, "Invalid username or password", null));
        } else if (e instanceof JwtTokenExpiredException) {
            mapper.writeValue(response.getWriter(), CommonResponse.of(HttpStatus.UNAUTHORIZED, ResponseCode.JWT_TOKEN_EXPIRED, "Token has expired", null));
        }

        mapper.writeValue(response.getWriter(), CommonResponse.of(HttpStatus.UNAUTHORIZED, ResponseCode.AUTHENTICATION, "Authentication failed", null));
    }
}
