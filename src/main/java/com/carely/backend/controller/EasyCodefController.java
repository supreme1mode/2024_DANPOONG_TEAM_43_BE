package com.carely.backend.controller;

import com.carely.backend.dto.easyCodef.AdditionalAuthDTO;
import com.carely.backend.dto.easyCodef.RequestUserIdentityDTO;
import com.carely.backend.dto.easyCodef.UserIdentityDTO;
import com.carely.backend.service.EasyCodef.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class EasyCodefController {
    private final EasyCodef easyCodef;
    private final EasyCodefProperties easyCodefProperties;
    private final String url = "/v1/kr/public/mw/identity-card/check-status";

    @PostMapping("/token")
    public String getToken() throws IOException {
        return easyCodef.requestToken(1);
    }

    @PostMapping("/connect")
    public String connectAPI(@RequestBody RequestUserIdentityDTO requestUserIdentityDTO) throws InterruptedException, UnsupportedEncodingException, JsonProcessingException {
        System.out.println(easyCodefProperties.getDemoAccessToken());

        UserIdentityDTO userIdentityDTO = UserIdentityDTO.builder()
                .organization("0002")
                .loginType("6")
                .loginTypeLevel("1")
                .telecom("")
                .phoneNo(requestUserIdentityDTO.getPhoneNo())
                .loginUserName(requestUserIdentityDTO.getUserName())
                .loginIdentity(requestUserIdentityDTO.getIdentity())
                .loginBirthDate("")
                .birthDate("")
                .identity(requestUserIdentityDTO.getIdentity())
                .userName(requestUserIdentityDTO.getUserName())
                .issueDate(requestUserIdentityDTO.getIssueDate())
                .identityEncYn("")
                .build();


        HashMap<String, Object> resultMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // DTO -> HashMap
            String json = mapper.writeValueAsString(userIdentityDTO);
            resultMap = mapper.readValue(json, HashMap.class);
            System.out.println("Deserialized HashMap: " + resultMap);

        } catch (Exception e) {
            e.printStackTrace();
        }



        String string = easyCodef.requestProduct(url, 1, resultMap);
        return string;
    }


    //추가인증 특이사항
    //
    //- 간편인증시 인증을 완료하지 않고 간편인증(simpleAuth)에 "1"을 입력할 경우 2번까지는 재시도, 3번 시도시 CF-12872 오류 발생
    @PostMapping("/additional-info")
    public String handleAdditionalAuth(@RequestBody AdditionalAuthDTO.TwoWayInfoDTO twoWayInfoDTO) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {

        AdditionalAuthDTO authDTO = AdditionalAuthDTO.builder()
                        .organization("0002")
                        .identity("0000000000000")
                        .userName("ddd")
                        .issueDate("ddddd")
                        .simpleAuth("1")
                        .is2Way((Boolean) true)
                        .twoWayInfo(twoWayInfoDTO)
                        .build();

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // DTO → JSON 변환
            String json = mapper.writeValueAsString(authDTO);
            System.out.println("Serialized JSON: " + json);

            // JSON → HashMap 변환
            resultMap = mapper.readValue(json, LinkedHashMap.class);
            System.out.println("Deserialized HashMap: " + resultMap);

            // 특정 값 확인
            System.out.println("is2Way Value: " + resultMap.get("is2Way"));
            System.out.println("TwoWayInfo: " + resultMap.get("twoWayInfo"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        String string = easyCodef.requestCertification(url, 1, resultMap);
        return string;
    }

}
