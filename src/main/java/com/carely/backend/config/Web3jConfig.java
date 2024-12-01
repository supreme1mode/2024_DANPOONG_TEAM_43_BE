package com.carely.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {
    //블록체인 서버 주소

    private String ganacheUrl = "http://3.34.24.211:7545";

    @Bean
    public Web3j web3j() {
        System.out.println("Using Ganache URL: " + ganacheUrl);
        return Web3j.build(new HttpService(ganacheUrl)); // Ganache의 URL
    }
}
