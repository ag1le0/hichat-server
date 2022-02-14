package com.foxconn.fii.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("security.oauth2")
public class OAuth2Properties {

    private ClientProperties client;

    private ResourceProperties resource;

    private String logoutUrl;

    @Data
    public static class ClientProperties {

        private String clientId;

        private String clientSecret;

        private String accessTokenUri;

        private String userAuthorizationUri;

        private String clientAuthenticationScheme;
    }

    @Data
    public static class ResourceProperties {

        private String userInfoUri;

        private String userChangePassUri;

        private String tokenInfoUri;
    }
}
