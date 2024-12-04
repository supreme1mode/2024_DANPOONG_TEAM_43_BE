package com.carely.backend.service.ocr;

import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.service.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OCRService {
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;

    public String uploadCertificateImage(MultipartFile file, String kakaoId) throws IOException {

        return s3Uploader.upload(file, "certificate");
    }



    //private final UserRepository userRepository;
    //private final CertificateService certificateService;

    @Value("${google.vision.api-key}")
    private String apiKey;

//    @Transactional
//    public CertificateDTO extractTextAlreadyUser(MultipartFile imageFile, String kakaoId) throws Exception {
//        // 유저 확인
//        User user = userRepository.findByKakaoId(kakaoId)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        // 이미지 파일을 Base64로 인코딩
//        byte[] imageBytes = imageFile.getBytes();
//        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//        // Google Vision API URL
//        String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;
//
//        // 요청 본문 생성
//        JSONObject image = new JSONObject();
//        image.put("content", base64Image);
//
//        JSONObject feature = new JSONObject();
//        feature.put("type", "TEXT_DETECTION");
//
//        JSONObject request = new JSONObject();
//        request.put("image", image);
//        request.put("features", new JSONArray().put(feature));
//
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("requests", new JSONArray().put(request));
//
//        // HTTP 요청 전송
//        URL url = new URL(visionApiUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//        connection.setDoOutput(true);
//
//        try (OutputStream os = connection.getOutputStream()) {
//            os.write(requestBody.toString().getBytes("UTF-8"));
//        }
//
//        // 응답 처리
//        InputStream responseStream = connection.getInputStream();
//        byte[] responseBytes = responseStream.readAllBytes();
//        String jsonResponse = new String(responseBytes, "UTF-8");
//
//        // JSON에서 텍스트 추출
//        String extractedText = parseResponse(jsonResponse);
//        System.out.println(extractedText);
//        // 텍스트에서 필요한 정보 추출
//        OCRResponseDto ocrResponseDto = extractFieldsUsingKeywords(extractedText);
//        System.out.println(ocrResponseDto.getName());
//
//        // 생년 월 일 분리
//        String extractedYear = ocrResponseDto.getBirth().substring(0, 4);  // "1987"
//        String extractedMonth = ocrResponseDto.getBirth().substring(6, 8); // "05"
//        String extractedDay = ocrResponseDto.getBirth().substring(10, 12); // "23"
//
//
//        String userYear = user.getIdentity().substring(0,2);
//        String userMonth = user.getIdentity().substring(2,4);
//        String userDay = user.getIdentity(
//
//        //OCR 정보와 현재 유저 정보가 일치하는지 확인
//        if (ocrResponseDto.getName().equals(user.getUsername()) && extractedYear.equals(user.getBirthyear()) && extractedMonth.equals(user.getBirthmonth()) && extractedDay.equals(user.getBirthday())) {
//            // 자격증 인증 서비스 호출
//            CertificateDTO certificateDTO = certificateService.getCertificateById(ocrResponseDto.getCertificateNum());
//
//            // 자격증에 적힌 정보와 OCR과 동일한지 확인
//            if (Objects.equals(certificateDTO.getCertificateId(), ocrResponseDto.getCertificateNum()) && Objects.equals(certificateDTO.getUsername(), ocrResponseDto.getName()) && Objects.equals(certificateDTO.getUserId(), user.getId().toString())) {
//                // 자격증 검증 성공
//                user.updateCertificateCheck();
//                return certificateDTO;
//            }
//
//        }
//        else {
//            throw new UserNotMatchException("유저 안 맞음");
//        }
//        return null;
//    }


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
    private OCRResponseDto extractFieldsUsingKeywords(String ocrResult) {
        Map<String, String> keywordPatterns = Map.of(
                "name", "성명",
                "birth", "생년월일",
                "certificateNum", "등록번호",
                "certificateType", "자격종목",
                "certificateDate", "취득일자",
                "certificateName", "취득과목"
        );

        OCRResponseDto.OCRResponseDtoBuilder builder = OCRResponseDto.builder();

        for (Map.Entry<String, String> entry : keywordPatterns.entrySet()) {
            String key = entry.getKey();
            String keyword = entry.getValue();
            String value = extractByKeyword(ocrResult, keyword);
            System.out.println(key + keyword + value);

            switch (key) {
                case "name": builder.name(value); break;
                case "birth": builder.birth(value); break;
                case "certificateNum": builder.certificateNum(value); break;
                case "certificateType": builder.certificateType(value); break;
                case "certificateDate": builder.certificateDate(value); break;
                case "certificateName": builder.certificateName(value); break;
            }
        }

        return builder.build();
    }

    private String extractByKeyword(String ocrResult, String keyword) {
        String[] lines = ocrResult.split("\\r?\\n"); // 개행으로 텍스트 분리
        for (int i = 0; i < lines.length; i++) {
            // 현재 라인에서 키워드를 찾음
            if (lines[i].contains(keyword)) {
                // 1. 현재 라인에서 키워드 뒤에 값이 있는지 확인
                Pattern pattern = Pattern.compile(Pattern.quote(keyword) + "\\s*:?\\s*(.*)");
                Matcher matcher = pattern.matcher(lines[i]);
                if (matcher.find() && !matcher.group(1).isEmpty()) {
                    return matcher.group(1).trim();
                }

                // 2. 값이 없다면, 다음 라인을 값으로 간주
                if (i + 1 < lines.length) {
                    return lines[i + 1].trim();
                }
            }
        }
        return null; // 키워드가 없는 경우 null 반환
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
