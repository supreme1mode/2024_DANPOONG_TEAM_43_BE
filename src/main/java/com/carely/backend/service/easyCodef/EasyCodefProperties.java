package com.carely.backend.service.easyCodef;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EasyCodefProperties {

    // 데모 엑세스 토큰 발급을 위한 클라이언트 아이디
    @Value("${demo.client_id}")
    private String demoClientId;

    // 데모 엑세스 토큰 발급을 위한 클라이언트 시크릿
    @Value("${demo.secret_key}")
    private String demoClientSecret;

    @Value("${demo.access_token}")
    private String demoAccessToken;

    @Setter
    @Value("${demo.public_key}")
    private String publicKey;

    /**
     * 클래스 초기화 후 값 설정 로직 실행
     */
    @PostConstruct
    public void initialize() {
        setClientInfoForDemo(this.demoClientId, this.demoClientSecret);
        System.out.println("EasyCodefProperties initialized with:");
        System.out.println("Client ID: " + this.demoClientId);
        System.out.println("Client Secret: " + this.demoClientSecret);
        System.out.println("Access Token: " + this.demoAccessToken);
        System.out.println("Public Key: " + this.publicKey);
    }

    public void setClientInfoForDemo(String demoClientId, String demoClientSecret) {
        this.demoClientId = demoClientId;
        this.demoClientSecret = demoClientSecret;
    }
}
