package com.hd.book.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    @Value("${aladin}")
    private String AUTH_TOKEN;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://www.aladin.co.kr")
                .build();
    }

    public String getAuthToken() {
        return AUTH_TOKEN;
    }
}
