package com.carely.backend.service.certificate;

import javax.net.ssl.*;

public class UnsafeTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        // 인증서 검증 생략
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        // 인증서 검증 생략
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0]; // 빈 배열 반환
    }
}
