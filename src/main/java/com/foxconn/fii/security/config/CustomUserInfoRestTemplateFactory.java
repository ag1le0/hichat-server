package com.foxconn.fii.security.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.oauth2.resource.DefaultUserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.RequestEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

@Component
public class CustomUserInfoRestTemplateFactory extends DefaultUserInfoRestTemplateFactory {

    private boolean trigger = true;

    public CustomUserInfoRestTemplateFactory(
            ObjectProvider<List<UserInfoRestTemplateCustomizer>> customizers,
            ObjectProvider<OAuth2ProtectedResourceDetails> details,
            ObjectProvider<OAuth2ClientContext> oauth2ClientContext) {
        super(customizers, details, oauth2ClientContext);
    }

    @Override
    public OAuth2RestTemplate getUserInfoRestTemplate() {
        OAuth2RestTemplate restTemplate = super.getUserInfoRestTemplate();
        if (trigger) {
            CustomAuthorizationCodeAccessTokenProvider accessTokenProvider = new CustomAuthorizationCodeAccessTokenProvider();
            accessTokenProvider.setTokenRequestEnhancer(new AcceptJsonRequestEnhancer());
            restTemplate.setAccessTokenProvider(accessTokenProvider);
            trigger = false;
        }
        return restTemplate;
    }

    static class AcceptJsonRequestEnhancer implements RequestEnhancer {

        @Override
        public void enhance(AccessTokenRequest request,
                            OAuth2ProtectedResourceDetails resource,
                            MultiValueMap<String, String> form, HttpHeaders headers) {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        }

    }
}
