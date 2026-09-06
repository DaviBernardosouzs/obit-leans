package com.example.orbitleans.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;


@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient nasaGibsRestClient(RestClient.Builder builder,
            @Value("${nasa.gibs.base-url}") String baseUrl,
            @Value("${http.connect-timeout}") Duration connectTimeout,
            @Value("${http.read-timeout}") Duration readTimeout) {
        return build(builder, baseUrl, connectTimeout, readTimeout).build();
    }

    @Bean
    public RestClient celesTrakRestClient(RestClient.Builder builder,
            @Value("${celestrak.base-url}") String baseUrl,
            @Value("${http.connect-timeout}") Duration connectTimeout,
            @Value("${http.read-timeout}") Duration readTimeout) {
        return build(builder, baseUrl, connectTimeout, readTimeout).build();
    }

    @Bean
    public RestClient nominatimRestClient(RestClient.Builder builder,
            @Value("${nominatim.base-url}") String baseUrl,
            @Value("${nominatim.user-agent}") String userAgent,
            @Value("${http.connect-timeout}") Duration connectTimeout,
            @Value("${http.read-timeout}") Duration readTimeout) {
        return build(builder, baseUrl, connectTimeout, readTimeout)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    private RestClient.Builder build(RestClient.Builder builder, String baseUrl,
            Duration connectTimeout, Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return builder.clone()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory);
    }
}
