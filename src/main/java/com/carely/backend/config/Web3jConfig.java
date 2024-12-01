package com.carely.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {
    //블록체인 서버 주소
    @Value("${ganache.url:http://host.docker.internal:7545}")
    private String ganacheUrl;
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(ganacheUrl)); // Ganache의 URL
    }
}
