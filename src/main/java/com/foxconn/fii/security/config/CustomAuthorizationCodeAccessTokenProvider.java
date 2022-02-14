package com.foxconn.fii.security.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

public class CustomAuthorizationCodeAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {

    @Override
    protected RestOperations getRestTemplate() {
        RestTemplate restTemplate = (RestTemplate) super.getRestTemplate();
        ClientHttpRequestFactory requestFactory = httpComponentsClientHttpRequestFactory();
        if (requestFactory != null) {
            restTemplate.setRequestFactory(requestFactory);
        }
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> {
                return true;
            };

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            CloseableHttpClient client = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
            clientHttpRequestFactory.setConnectTimeout(30 * 1000);
            clientHttpRequestFactory.setReadTimeout(3 * 60 * 1000);
            return clientHttpRequestFactory;
        } catch (Exception e) {
            return null;
        }
    }
}
