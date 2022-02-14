package com.foxconn.fii.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LanguageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest &&
                servletResponse instanceof HttpServletResponse &&
                ((HttpServletRequest) servletRequest).getRequestURI().contains("/login")) {
            log.debug("### filter language process");
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String lang = request.getParameter("lang");
            if (!StringUtils.isEmpty(lang)) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.addCookie(new Cookie("lang", lang));
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
