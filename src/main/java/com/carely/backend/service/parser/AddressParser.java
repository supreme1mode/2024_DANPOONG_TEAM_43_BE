package com.carely.backend.service.parser;

import com.carely.backend.exception.NotValidAddressException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddressParser {


    public static String[] parseAddress(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode documents = root.get("documents").get(0); // 첫 번째 결과 사용

            String region1 = documents.get("address").get("region_1depth_name").asText(); // 도
            String region2 = documents.get("address").get("region_2depth_name").asText(); // 시/군
            String region3 = documents.get("address").get("region_3depth_name").asText(); // 동/면/읍

            return new String[]{region1, region2, region3};
        } catch (Exception e) {
            throw new NotValidAddressException("Failed to parse address from response");
        }
    }
}