package com.foxconn.fii.config;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.TimeZone;

public class AcceptHeaderResolver extends SessionLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        if (StringUtils.isEmpty(lang)) {
            Cookie langCookie = WebUtils.getCookie(request, "lang");
            if (langCookie == null) {
                if (!StringUtils.isEmpty(request.getHeader("Accept-Language"))) {
                    lang = request.getHeader("Accept-Language");
                } else {
                    return super.resolveLocale(request);
                }
            } else {
                lang = langCookie.getValue();
            }
        }
        return Locale.forLanguageTag(lang);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext() {
            public Locale getLocale() {
                Locale locale = resolveLocale(request);
                if (locale == null) {
                    locale = determineDefaultLocale(request);
                }

                return locale;
            }

            @Nullable
            public TimeZone getTimeZone() {
                TimeZone timeZone = (TimeZone)WebUtils.getSessionAttribute(request, TIME_ZONE_SESSION_ATTRIBUTE_NAME);
                if (timeZone == null) {
                    timeZone = determineDefaultTimeZone(request);
                }

                return timeZone;
            }
        };
    }
}
