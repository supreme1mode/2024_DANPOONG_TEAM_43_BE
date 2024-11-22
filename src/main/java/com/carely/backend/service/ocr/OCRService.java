package com.carely.backend.service.ocr;

import com.carely.backend.domain.User;
import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.exception.UserNotFoundException;
import com.carely.backend.exception.UserNotMatchException;
import com.carely.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OCRService {
    private final UserRepository userRepository;

    @Value("${google.vision.api-key}")
    private String apiKey;

    @Transactional
    public OCRResponseDto extractText(MultipartFile imageFile, String username) {
        User user = userRepository.findByKakaoId(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        try {
            // 이미지 파일을 Base64로 인코딩
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Google Vision API URL
            String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

            // 요청 본문 생성
            JSONObject image = new JSONObject();
            image.put("content", base64Image);

            JSONObject feature = new JSONObject();
            feature.put("type", "TEXT_DETECTION");

            JSONObject request = new JSONObject();
            request.put("image", image);
            request.put("features", new JSONArray().put(feature));

            JSONObject requestBody = new JSONObject();
            requestBody.put("requests", new JSONArray().put(request));

            // HTTP 요청 전송
            URL url = new URL(visionApiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            // 응답 처리
            InputStream responseStream = connection.getInputStream();
            byte[] responseBytes = responseStream.readAllBytes();
            String jsonResponse = new String(responseBytes, "UTF-8");

            // JSON에서 텍스트 추출
            String extractedText = parseResponse(jsonResponse);

            // 텍스트에서 필요한 정보 추출
            return extractFields(extractedText, user);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // JSON 응답에서 텍스트 추출
    private String parseResponse(String jsonResponse) {
        JSONObject response = new JSONObject(jsonResponse);
        JSONArray responses = response.getJSONArray("responses");
        if (responses.isEmpty()) {
            return "No responses found";
        }

        JSONObject firstResponse = responses.getJSONObject(0);
        JSONArray textAnnotations = firstResponse.optJSONArray("textAnnotations");
        if (textAnnotations == null || textAnnotations.isEmpty()) {
            return "No text annotations found";
        }

        // 전체 텍스트 가져오기
        return textAnnotations.getJSONObject(0).getString("description");
    }

    // 정규식을 사용하여 필드 추출
    private OCRResponseDto extractFields(String ocrResult, User user) {
        // 정규식 패턴
        String namePattern = "성명\\s*(\\S+)";
        String dobPattern = "생년월일\\s*([\\d]{4}년\\s*[\\d]{2}월\\s*[\\d]{2}일)";
        String regNumberPattern = "등록 번호\\s*([A-Z0-9]+)";
        String categoryPattern = "자격종목\\s*(\\S+)";
        String acquisitionDatePattern = "취 득 일 자\\s*([\\d]{4}년\\s*[\\d]{2}월\\s*[\\d]{2}일)";
        String subjectPattern = "취득과목\\s*([\\S ]+)";

        if (user.getUsername().equals(extractByPattern(ocrResult, namePattern))){
            return OCRResponseDto.builder()
                    .name(extractByPattern(ocrResult, namePattern))
                    .birth(extractByPattern(ocrResult, dobPattern))
                    .certificateNum(extractByPattern(ocrResult, regNumberPattern))
                    .certificateType(extractByPattern(ocrResult, categoryPattern))
                    .certificateDate(extractByPattern(ocrResult, acquisitionDatePattern))
                    .certificateName(extractByPattern(ocrResult, subjectPattern))
                    .build();
        }
        else {
            throw new UserNotMatchException("자격증의 정보가 가입한 회원정보와 일치하지 않습니다.");
        }
    }

    // 정규식 기반으로 텍스트 추출
    private String extractByPattern(String text, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "정보 없음";
    }
}
