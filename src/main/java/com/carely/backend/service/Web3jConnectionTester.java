package com.carely.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

@Component
public class Web3jConnectionTester implements CommandLineRunner {

    @Autowired
    private Web3j web3j;

    @Override
    public void run(String... args) throws Exception {
        try {
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().send();
            System.out.println("Connected to Ethereum client version: " + clientVersion.getWeb3ClientVersion());
        } catch (Exception e) {
            System.err.println("Failed to connect to Ethereum client: " + e.getMessage());
        }
    }
}


