package com.carely.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("http://13.124.232.105:7545")); // Ganache의 URL
    }
}