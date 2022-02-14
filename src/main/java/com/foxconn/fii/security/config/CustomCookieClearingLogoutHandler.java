package com.foxconn.fii.security.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class CustomCookieClearingLogoutHandler implements LogoutHandler {

    private final List<String> cookiesToClear;

    public CustomCookieClearingLogoutHandler(String... cookiesToClear) {
        Assert.notNull(cookiesToClear, "List of cookies cannot be null");
        this.cookiesToClear = Arrays.asList(cookiesToClear);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Iterator var4 = this.cookiesToClear.iterator();

        while (var4.hasNext()) {
            String cookieName = (String) var4.next();
            Cookie cookie = new Cookie(cookieName, (String) null);
            String cookiePath = "/";
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

    }
}
