package com.foxconn.fii.security.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    private String domain;

    public CustomLogoutSuccessHandler(String domain, String defaultTargetUrl) {
        this.domain = domain;
        this.setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {
        String requestUrl = httpServletRequest.getRequestURL().toString();
        String defaultTargetUrl = this.getDefaultTargetUrl();
        if (!requestUrl.startsWith(domain)) {
            defaultTargetUrl = "/sign-in";
        } else if (!defaultTargetUrl.contains("redirectUrl")) {
            defaultTargetUrl = defaultTargetUrl + String.format("?redirectUrl=%s", requestUrl.replace("/logout", ""));
        }
        this.setDefaultTargetUrl(defaultTargetUrl);
        this.handle(httpServletRequest, httpServletResponse, authentication);
    }
}
