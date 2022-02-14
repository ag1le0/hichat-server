package com.foxconn.fii.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Value("${server.http.port}")
    private int httpPort;

    @Value("${server.port}")
    private int port;

    @Bean
    @Primary
    public ClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws Exception {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

//        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
//        poolingConnectionManager.setMaxTotal(5);
//        poolingConnectionManager.setDefaultMaxPerRoute(4);

//        CloseableHttpClient client = HttpClientBuilder.create()
//                .setConnectionManager(poolingConnectionManager)
//                .setSSLSocketFactory(csf)
//                .build();

        CloseableHttpClient client = HttpClients.custom()
//                .setConnectionManager(poolingConnectionManager)
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        clientHttpRequestFactory.setConnectTimeout(30 * 1000);
        clientHttpRequestFactory.setReadTimeout(3 * 60 * 1000);
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder
//                .requestFactory(new ClientHttpRequestFactorySupplier())
////                .setConnectTimeout(Duration.ofSeconds(30))
////                .setReadTimeout(Duration.ofMinutes(3))
//                .build();
//    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
//        connector.setSecure(false);
//        connector.setRedirectPort(port);
        return connector;
    }

}
