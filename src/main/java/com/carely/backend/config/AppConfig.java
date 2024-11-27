package com.carely.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean(name = "customRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
