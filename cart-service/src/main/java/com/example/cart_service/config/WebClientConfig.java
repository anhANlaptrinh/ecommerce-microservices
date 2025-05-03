package com.example.cart_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient productWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://product-service:8081/api/products")
                .build();
    }
}
