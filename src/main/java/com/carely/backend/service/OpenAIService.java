package com.carely.backend.service;

import com.carely.backend.dto.openai.CompletionRequestDTO;
import com.carely.backend.dto.openai.CompletionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    @Value("${openai.model}")

    private String model;

    public Map<String, String> getSummary(String content, String aiSummary) {
        String url = "https://api.openai.com/v1/chat/completions";
        CompletionRequestDTO.Message message = CompletionRequestDTO.Message.builder()
                .role("user")
                .content("기존 문장은 " + aiSummary + "이거야." + content + "라는 새로운 문장에서 간병하는 데 필요한 핵심 정보만을 추출하여 기존 정보와 함께 객관적으로 요약 정리해줘. " +
                        "단순한 오늘 상태의 요약이 아니라 환자의 객관적인 정보, 증상만을 존댓말로 요약해야해. 그 외 중요하지 않은 정보는 모두 정리하지 마. " +
                        "오늘, 시간 등 시간을 특정할 수 있는 용어는 빼서 존댓말로 요약해. " +
                        "비슷한 카테고리에서 이전보다 기존 문장보다 새로운 문장에서 개선이 이루어졌다면(식사량 증가 등) 개선된 내용을 위주로 존댓말로 작성해줘. " +
                        "최대한 중복을 배제해. 꼭 '부드러운 존댓말' 문장으로 제시한 정보 외에 유추나 추가 내용은 작성하지 마." +
                        "전체 요약(all), 체온 및 건강 상태(healthy), 식사 및 약물 복용(eat), 추가적인 건강 상태(additionalHealth), 정서 및 사회적 상호작용(social), 배뇨 상태(voiding) 총 6가지 카테고리를 \n\n 두개의 개행문자로 분리해서 가져와줘." +
                        "수식어 넣지 말고 그냥 문장으로만 작성해줘. 전체 요약(all): 처럼 하지 마.")
                .build();
        CompletionRequestDTO requestDto = CompletionRequestDTO.builder()
                .model(model)
                .messages(Collections.singletonList(message))
                .temperature(0.8f)
                .build();
        HttpEntity<CompletionRequestDTO> requestEntity = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<CompletionResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, CompletionResponseDTO.class);

        if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
            String result = response.getBody().getChoices().get(0).getMessage().getContent();
            return parseResponse(result);
        }
        return Collections.emptyMap();
    }

    private Map<String, String> parseResponse(String response) {
        String[] parts = response.split("\n\n");
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("all", parts[0].trim());
        resultMap.put("healthy", parts[1].trim());
        resultMap.put("eat", parts[2].trim());
        resultMap.put("additionalHealth", parts[3].trim());
        resultMap.put("social", parts[4].trim());
        resultMap.put("voiding", parts[5].trim());
        return resultMap;
    }

}