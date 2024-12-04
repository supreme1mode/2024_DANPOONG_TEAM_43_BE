package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.EasyCodefAPI;
import com.carely.backend.dto.easyCodef.AdditionalAuthDTO;
import com.carely.backend.dto.easyCodef.RequestUserIdentityDTO;
import com.carely.backend.dto.easyCodef.UserIdentityDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.exception.IdentityNotAcceptableException;
import com.carely.backend.exception.ObjectNullException;
import com.carely.backend.service.CacheService;
import com.carely.backend.service.EasyCodef.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class EasyCodefController implements EasyCodefAPI {
    private final EasyCodef easyCodef;
    private final EasyCodefProperties easyCodefProperties;
    private final String url = "/v1/kr/public/mw/identity-card/check-status";

    @PostMapping("/token")
    public String getToken() throws IOException {
        return easyCodef.requestToken(1);
    }

    @PostMapping("/connect")
    public ResponseEntity<ResponseDTO> connectAPI(@RequestBody RequestUserIdentityDTO requestUserIdentityDTO) throws InterruptedException, UnsupportedEncodingException, JsonProcessingException {
        System.out.println(easyCodefProperties.getDemoAccessToken());

        // UserIdentityDTO 생성
        UserIdentityDTO userIdentityDTO = UserIdentityDTO.builder()
                .organization("0002")
                .loginType("6")
                .loginTypeLevel("1")
                .telecom("")
                .phoneNo(requestUserIdentityDTO.getPhoneNum())
                .loginUserName(requestUserIdentityDTO.getNickname())
                .loginIdentity(requestUserIdentityDTO.getIdentity())
                .loginBirthDate("")
                .birthDate("")
                .identity(requestUserIdentityDTO.getIdentity())
                .userName(requestUserIdentityDTO.getNickname())
                .issueDate(requestUserIdentityDTO.getIssueDate())
                .identityEncYn("")
                .build();

        // DTO → HashMap 변환
        HashMap<String, Object> resultMap = new ObjectMapper().convertValue(userIdentityDTO, HashMap.class);

        // /connect 처리
        EasyCodefResponse response = easyCodef.requestProduct(url, 1, resultMap);
        System.out.println("Connect API Response: " + response);

        // 'data' 객체 가져오기
        Map<String, Object> dataMap = (Map<String, Object>) response.get("data");

        // 'jti' 값 및 타임스탬프 가져오기
        String jti = (String) dataMap.get("jti");
        Long twoWayTimestamp = (Long) dataMap.get("twoWayTimestamp");

        // 동기식 응답 처리를 위한 CountDownLatch 생성
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ResponseDTO> finalResponse = new AtomicReference<>();

        // 7초 뒤 /additional-info 호출
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            try {
                // 추가 인증 DTO 생성
                AdditionalAuthDTO authDTO = AdditionalAuthDTO.builder()
                        .organization(userIdentityDTO.getOrganization())
                        .identity(userIdentityDTO.getIdentity())
                        .userName(userIdentityDTO.getUserName())
                        .issueDate(userIdentityDTO.getIssueDate())
                        .simpleAuth("1")
                        .is2Way(true)
                        .twoWayInfo(AdditionalAuthDTO.TwoWayInfoDTO.builder()
                                .jti(jti)
                                .twoWayTimestamp(twoWayTimestamp)
                                .jobIndex(0)
                                .threadIndex(0)
                                .build())
                        .build();

                // 추가 인증 로직
                LinkedHashMap<String, Object> twoResultMap = new ObjectMapper().convertValue(authDTO, LinkedHashMap.class);
                EasyCodefResponse additionalResponse = easyCodef.requestCertification(url, 1, twoResultMap);

                // 인증 결과 처리
                Map<String, Object> resultMapper = (Map<String, Object>) additionalResponse.get("result");
                String code = (String) resultMapper.get("code");
                System.out.println("Additional Info API Response Code: " + code);

                if (!Objects.equals(code, "CF-00000")) {
                    throw new IdentityNotAcceptableException("안 됨");
                } else {
                    finalResponse.set(new ResponseDTO(SuccessCode.SUCCESS_GET_IDENTITY, "Authentication successful"));
                }
            } catch (Exception e) {
                System.err.println("추가 인증 중 오류 발생: " + e.getMessage());
                throw new IdentityNotAcceptableException("안 됨");
            } finally {
                latch.countDown(); // 작업 완료 신호
                scheduler.shutdown(); // 스케줄러 종료
                throw new IdentityNotAcceptableException("안 됨");

            }
        }, 7, TimeUnit.SECONDS);

        // 작업 완료 대기
        latch.await();

        // 최종 응답 반환
        return ResponseEntity
                .status(SuccessCode.SUCCESS_GET_IDENTITY.getStatus().value())
                .body(finalResponse.get());
    }

    //추가인증 특이사항
    //
    //- 간편인증시 인증을 완료하지 않고 간편인증(simpleAuth)에 "1"을 입력할 경우 2번까지는 재시도, 3번 시도시 CF-12872 오류 발생
//    @PostMapping("/additional-info")
//    public EasyCodefResponse handleAdditionalAuth(@RequestBody AdditionalAuthDTO.TwoWayInfoDTO twoWayInfoDTO) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
//        if (cacheService.get(twoWayInfoDTO.getJti()) == null) {
//            throw new ObjectNullException("캐시 쓴 거임");
//        }
//        UserIdentityDTO userIdentityDTO = (UserIdentityDTO) cacheService.get(twoWayInfoDTO.getJti());
//        AdditionalAuthDTO authDTO = AdditionalAuthDTO.builder()
//                .organization(userIdentityDTO.getOrganization())
//                .identity(userIdentityDTO.getIdentity())
//                .userName(userIdentityDTO.getUserName())
//                .issueDate(userIdentityDTO.getIssueDate())
//                .simpleAuth("1")
//                .is2Way(true)
//                .twoWayInfo(twoWayInfoDTO)
//                .build();
//
//
//        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            // DTO → JSON 변환
//            String json = mapper.writeValueAsString(authDTO);
//            System.out.println("Serialized JSON: " + json);
//
//            // JSON → HashMap 변환
//            resultMap = mapper.readValue(json, LinkedHashMap.class);
//            System.out.println("Deserialized HashMap: " + resultMap);
//
//            // 특정 값 확인
//            System.out.println("is2Way Value: " + resultMap.get("is2Way"));
//            System.out.println("TwoWayInfo: " + resultMap.get("twoWayInfo"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        EasyCodefResponse string = easyCodef.requestCertification(url, 1, resultMap);
//
//        // 'result' 객체 가져오기
//        Map<String, Object> dataMap = (Map<String, Object>) string.get("result");
//
//        // 'code' 값 가져오기
//        String code = (String) dataMap.get("code");
//
//        if (!Objects.equals(code, "CF-00000")) { // 인증이 안 된 거임.
//            cacheService.clear(authDTO.getIdentity());
//        }
//        return string;
//    }

}
