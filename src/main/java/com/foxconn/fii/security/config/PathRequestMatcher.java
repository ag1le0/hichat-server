package com.foxconn.fii.security.config;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher securedMatchers;
    private OrRequestMatcher ignoredMatchers;

    public PathRequestMatcher(String[] securedPaths) {
        if (securedPaths == null) {
            throw new IllegalArgumentException("paths must be not null.");
        }

        List<RequestMatcher> securedRequestMatcher = Stream.of(securedPaths).map(AntPathRequestMatcher::new).collect(Collectors.toList());
        securedMatchers = new OrRequestMatcher(securedRequestMatcher);
    }

    public PathRequestMatcher(String[] securedPaths, String[] ignoredPaths) {
        if (securedPaths == null && ignoredPaths == null) {
            throw new IllegalArgumentException("paths must be not null.");
        }

        if (securedPaths != null) {
            List<RequestMatcher> securedRequestMatcher = Stream.of(securedPaths).map(AntPathRequestMatcher::new).collect(Collectors.toList());
            securedMatchers = new OrRequestMatcher(securedRequestMatcher);
        }

        if (ignoredPaths != null) {
            List<RequestMatcher> ignoredRequestMatcher = Stream.of(ignoredPaths).map(AntPathRequestMatcher::new).collect(Collectors.toList());
            ignoredMatchers = new OrRequestMatcher(ignoredRequestMatcher);
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (ignoredMatchers != null && ignoredMatchers.matches(request)) {
            return false;
        } else {
            return securedMatchers != null && securedMatchers.matches(request);
        }
    }
}
