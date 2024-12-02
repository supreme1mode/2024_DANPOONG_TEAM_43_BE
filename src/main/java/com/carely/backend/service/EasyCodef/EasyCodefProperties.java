package com.carely.backend.service.EasyCodef;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * io.codef.easycodef
 *   |_ EasyCodefProperties.java
 * </pre>
 *
 * Desc : 코드에프의 쉬운 사용을 위한 프로퍼티 클래스
 * @Company : ?CODEF corp.
 * @Author  : notfound404@codef.io
 * @Date    : Jun 26, 2020 3:36:51 PM
 */
@Getter
@Configuration
public class EasyCodefProperties {

    //	데모 엑세스 토큰 발급을 위한 클라이언트 아이디
    @Value("${demo.client_id}")
    private String demoClientId;

    //	데모 엑세스 토큰 발급을 위한 클라이언트 시크릿
    @Value("${demo.secert_key}")
    private String demoClientSecret;


    @Value("${demo.access_token}")
    private String demoAccessToken;

    @Setter
    @Value("${demo.public_key}")
    private String publicKey;

    public void setClientInfoForDemo(String demoClientId, String demoClientSecret) {
        this.demoClientId = demoClientId;
        this.demoClientSecret = demoClientSecret;
    }
}