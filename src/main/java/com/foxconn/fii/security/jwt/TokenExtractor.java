package com.foxconn.fii.security.jwt;

public interface TokenExtractor {
    String extract(String payload);
}
