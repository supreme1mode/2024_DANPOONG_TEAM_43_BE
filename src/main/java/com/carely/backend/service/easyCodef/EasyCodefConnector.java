package com.carely.backend.service.easyCodef;

import com.carely.backend.code.EasyCodefMessageConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * <pre>
 * io.codef.easycodef
 *   |_ EasyCodefConnector.java
 * </pre>
 *
 * Desc : CODEF 엑세스 토큰 및 상품 조회를 위한 HTTP 요청 클래스
 * @Company : ©CODEF corp.
 * @Author  : notfound404@codef.io
 * @Date    : Jun 26, 2020 3:35:17 PM
 */
@Component
public class EasyCodefConnector {
    private static ObjectMapper mapper = new ObjectMapper();
    private static final int REPEAT_COUNT = 3;

    /**
     * Desc : CODEF 상품 조회 요청
     * @Company : ©CODEF corp.
     * @Author  : notfound404@codef.io
     * @Date    : Jun 26, 2020 3:35:26 PM
     */
    @SuppressWarnings("unchecked")
    public static EasyCodefResponse execute(String urlPath, int serviceType, HashMap<String, Object> bodyMap, EasyCodefProperties properties) throws InterruptedException {
        /**	#1.토큰 체크	*/
        String domain = EasyCodefConstant.DEMO_DOMAIN;
        String clientId = properties.getDemoClientId();
        String clientSecret = properties.getDemoClientSecret();
        String accessToken = getToken(clientId, clientSecret); // 토큰 반환

        /**	#2.요청 파라미터 인코딩	*/
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(bodyMap);
            bodyString = URLEncoder.encode(bodyString, "UTF-8");
        } catch (JsonProcessingException e) {
            return new EasyCodefResponse(EasyCodefMessageConstant.INVALID_JSON);
        } catch (UnsupportedEncodingException e) {
            return new EasyCodefResponse(EasyCodefMessageConstant.UNSUPPORTED_ENCODING);
        }

        /**	#3.상품 조회 요청	*/
        HashMap<String, Object> responseMap = requestProduct(domain + urlPath, accessToken, bodyString);
        if(EasyCodefConstant.INVALID_TOKEN.equals(responseMap.get("error")) || "CF-00401".equals(((HashMap<String, Object>)responseMap.get(EasyCodefConstant.RESULT)).get(EasyCodefConstant.CODE))){	// 액세스 토큰 유효기간 만료되었을 경우 토큰 재발급 후 상품 조회 요청 진행
            EasyCodefTokenMap.setToken(clientId, null);		// 토큰 정보 초기화
            accessToken = getToken(clientId, clientSecret); // 토큰 설정
            responseMap = requestProduct(domain + urlPath, accessToken, bodyString);
            System.out.println(responseMap);
            System.out.println(accessToken);
        } else if (EasyCodefConstant.ACCESS_DENIED.equals(responseMap.get("error")) || "CF-00403".equals(((HashMap<String, Object>)responseMap.get(EasyCodefConstant.RESULT)).get(EasyCodefConstant.CODE))) {	// 접근 권한이 없는 경우 - 오류코드 반환
            return new EasyCodefResponse(EasyCodefMessageConstant.UNAUTHORIZED, EasyCodefConstant.ACCESS_DENIED);
        }

        /**	#4.상품 조회 결과 반환	*/
        return new EasyCodefResponse(responseMap);
    }

    /**
     * Desc : CODEF HTTP POST 요청
     * @Company : ©CODEF corp.
     * @Author  : notfound404@codef.io
     * @Date    : Jun 26, 2020 3:35:34 PM
     */
    private static HashMap<String, Object> requestProduct(String urlPath, String token, String bodyString) {
        BufferedReader br = null;
        try {
            // HTTP 요청을 위한 URL 오브젝트 생성
            URL url = new URL(urlPath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");

            if (token != null && !token.isEmpty()) {
                con.setRequestProperty("Authorization", "Bearer " + token);		// 엑세스 토큰 헤더 설정
            }

            // 리퀘스트 바디 전송
            OutputStream os = con.getOutputStream();
            if (bodyString != null && !bodyString.isEmpty()) {
                os.write(bodyString.getBytes());
            }
            os.flush();
            os.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                return new EasyCodefResponse(EasyCodefMessageConstant.BAD_REQUEST, urlPath);
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return new EasyCodefResponse(EasyCodefMessageConstant.UNAUTHORIZED, urlPath);
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                return new EasyCodefResponse(EasyCodefMessageConstant.FORBIDDEN, urlPath);
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return new EasyCodefResponse(EasyCodefMessageConstant.NOT_FOUND, urlPath);
            } else {
                return new EasyCodefResponse(EasyCodefMessageConstant.SERVER_ERROR, urlPath);
            }

            // 응답 바디 read
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            System.out.println(responseStr);
            while ((inputLine = br.readLine()) != null) {
                responseStr.append(inputLine);
            }
            br.close();

            // 결과 반환
            return mapper.readValue(URLDecoder.decode(responseStr.toString(), "UTF-8"), new TypeReference<HashMap<String, Object>>(){});
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new EasyCodefResponse(EasyCodefMessageConstant.LIBRARY_SENDER_ERROR, e.getMessage());
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Desc : 엑세스 토큰 반환
     * @Company : ©CODEF corp.
     * @Author  : notfound404@codef.io
     * @Date    : Jun 26, 2020 3:35:47 PM
     */
    private static String getToken(String clientId, String clientSecret) throws InterruptedException {
        int i = 0;
        String accessToken = EasyCodefTokenMap.getToken(clientId);
        if(accessToken == null || accessToken.isEmpty() || !checkToken(accessToken)) { //만료 조건 추가
            while(true) {	// 토큰 발급 요청은 최대 3회까지 재시도
                HashMap<String, Object> tokenMap = publishToken(clientId, clientSecret);	// 토큰 발급 요청
                if(tokenMap != null) {
                    String newToken = (String)tokenMap.get("access_token");
                    EasyCodefTokenMap.setToken(clientId, newToken);	// 토큰 저장
                    accessToken = newToken;

                }

                break;    // 정상 발급시 반복문 종료

            }
        }

        return accessToken;
    }

    /**
     * Desc : CODEF 엑세스 토큰 발급 요청
     * @Company : ©CODEF corp.
     * @Author  : notfound404@codef.io
     * @Date    : Jun 26, 2020 3:36:01 PM
     */
    public static HashMap<String, Object> publishToken(String clientId, String clientSecret) {
        BufferedReader br = null;
        try {
            // HTTP 요청을 위한 URL 오브젝트 생성
            URL url = new URL(EasyCodefConstant.OAUTH_DOMAIN + EasyCodefConstant.GET_TOKEN);
            String params = "grant_type=client_credentials&scope=read";	// Oauth2.0 사용자 자격증명 방식(client_credentials) 토큰 요청 설정

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 클라이언트아이디, 시크릿코드 Base64 인코딩
            String auth = clientId + ":" + clientSecret;
            byte[] authEncBytes = Base64.encodeBase64(auth.getBytes());
            String authStringEnc = new String(authEncBytes);
            String authHeader = "Basic " + authStringEnc;

            con.setRequestProperty("Authorization", authHeader);
            con.setDoInput(true);
            con.setDoOutput(true);

            // 리퀘스트 바디 전송
            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {	// 정상 응답
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {	 // 에러 발생
                return null;
            }

            // 응답 바디 read
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                responseStr.append(inputLine);
            }
            br.close();

            return mapper.readValue(URLDecoder.decode(responseStr.toString(), "UTF-8"), new TypeReference<HashMap<String, Object>>(){});
        } catch (Exception e) {
            return null;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) { }
            }
        }
    }

    /**
     * 토큰 유효기간 확인
     */
    private static boolean checkToken(String accessToken) {
        HashMap<String, Object> tokenMap = null;
        try {
            tokenMap = EasyCodefUtil.getTokenMap(accessToken);
        } catch (IOException e) {
            // 확인 중 오류 발생 시
            return false;
        }
        // 토큰의 유효 기간 확인
        return EasyCodefUtil.checkValidity((int) (tokenMap.get("exp")));
    }
}