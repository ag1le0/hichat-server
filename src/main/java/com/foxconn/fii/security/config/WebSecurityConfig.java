package com.foxconn.fii.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.security.jwt.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
//@EnableOAuth2Sso
@EnableConfigurationProperties(OAuth2Properties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.domain}")
    private String domain;

    @Autowired
    private OAuth2Properties oauth2Properties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private ObjectMapper objectMapper;

    protected JwtAuthenticationFilter buildJwtTokenAuthenticationProcessingFilter(String[] securedPaths, String[] ignoredPaths) throws Exception {
        PathRequestMatcher matcher = new PathRequestMatcher(securedPaths, ignoredPaths);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(failureHandler, matcher, oauth2Properties, tokenExtractor, restTemplate, objectMapper);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] securedList = {
                "/**"
        };

        String[] ignoredList = {
                "/login**",
                "/sign-in",
                "/api/user/sign-in",
                "/api/group/official/send-text",
                "/api/group/official/send-media",
                "/api/group/official/send-highlight",
                "/api/group/official/send-alarm",
                "/api/group/official/send-report",
                "/api/module/desktop/list"
        };

        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers(ignoredList).permitAll()
//                .antMatchers(securedPageList)
//                .hasRole("WS_USER")
//                .antMatchers(securedEndpointList)
//                .hasRole("WS_USER")
//                .anyRequest()..authenticated()
                .anyRequest().hasAnyRole("OAUTH_USER", "WS_USER")
                .and()
                .formLogin().loginPage("/sign-in").successForwardUrl("/home")
        ;

        http.logout()
                .invalidateHttpSession(true)
                .addLogoutHandler(new CustomCookieClearingLogoutHandler("PEACHATSESSION", "access_token", "refresh_token"))
                .deleteCookies("PEACHATSESSION", "access_token", "refresh_token")
//                .logoutSuccessUrl(String.format("%s?redirectUrl=%s", oauth2Properties.getLogoutUrl(), domain))
//                .logoutSuccessHandler(new CustomLogoutSuccessHandler(domain, oauth2Properties.getLogoutUrl()));
                .logoutSuccessUrl("/sign-in")
        ;

        http.csrf().disable();

        http.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(securedList, ignoredList), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LanguageFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/assets/**", "/templates/**", "/js/**", "/WEB-INF/jsp/**");
        web.ignoring().antMatchers("/assets/**");
    }

}
