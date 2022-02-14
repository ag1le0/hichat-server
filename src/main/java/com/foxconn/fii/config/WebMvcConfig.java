package com.foxconn.fii.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${path.data}")
    private String dataPath;

    @Bean
    public MessageSource messageSource() {
        CustomReloadableResourceBundleMessageSource messageSource= new CustomReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
//        SessionLocaleResolver slr = new SessionLocaleResolver();
//        slr.setDefaultLocale(Locale.US);
//        return slr;
        return new AcceptHeaderResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**", "/favicon.ico", "/sitemap*.xml", "/templates/**", "/js/**")
                .addResourceLocations("classpath:/static/assets/", "/WEB-INF/jsp/", "/WEB-INF/templates/", "/WEB-INF/js/")
//                .setCacheControl(CacheControl.maxAge(7L, TimeUnit.DAYS).cachePublic())
                .resourceChain(true)
//                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
//                .addTransformer(new AppCacheManifestTransformer())
        ;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}
