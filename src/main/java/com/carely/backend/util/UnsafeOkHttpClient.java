package com.carely.backend.util;

import com.carely.backend.service.certificate.UnsafeTrustManager;
import okhttp3.OkHttpClient;
import java.security.SecureRandom;
import javax.net.ssl.*;

public class UnsafeOkHttpClient {

    public static OkHttpClient create() {
        try {
            // TrustManager 배열 설정
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new UnsafeTrustManager()
            };

            // SSLContext 초기화
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // OkHttpClient 빌더 설정
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // 모든 호스트네임 허용
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
