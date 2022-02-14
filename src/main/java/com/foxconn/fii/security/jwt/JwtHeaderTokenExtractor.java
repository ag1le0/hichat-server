package com.foxconn.fii.security.jwt;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.foxconn.fii.security.config.JwtProperties.BEARER_TOKEN_PREFIX;

@Component
public class JwtHeaderTokenExtractor implements TokenExtractor {

    @Override
    public String extract(String header) {
        if (StringUtils.isEmpty(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (!header.startsWith(BEARER_TOKEN_PREFIX)) {
            throw new AuthenticationServiceException("Invalid authorization header format.");
        }

        return header.substring(BEARER_TOKEN_PREFIX.length());
    }

}