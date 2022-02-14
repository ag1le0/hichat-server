package com.foxconn.fii.security.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.X509Certificate;

//@Component
////@ConditionalOnBean(RemoteTokenServices.class)
public class CustomRemoteTokenServices {

    @Autowired
    private RemoteTokenServices remoteTokenServices;

    @PostConstruct
    public void postConstruct(){
        RestTemplate restTemplate = new RestTemplate();
        ((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
        restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory());
        remoteTokenServices.setRestTemplate(restTemplate);
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
