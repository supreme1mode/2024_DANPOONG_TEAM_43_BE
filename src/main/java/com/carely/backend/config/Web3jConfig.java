package com.carely.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {
    //블록체인 서버 주소
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("http://127.0.0.1:7545")); // Ganache의 URL
    }
}
