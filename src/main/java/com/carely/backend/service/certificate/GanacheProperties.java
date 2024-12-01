package com.carely.backend.service.certificate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ganache")
public class GanacheProperties {
    private String contractKey;
    private String privateKey;
    @PostConstruct
    public void logProperties() {
        System.out.println("Contract Key: " + contractKey);
        System.out.println("Private Key: " + privateKey);
    }
    // Getter와 Setter 추가
    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
