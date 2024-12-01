package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.CertificateAPI;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateController implements CertificateAPI {

    private final CertificateService certificateService;

    // 봉사 세션 추가
    @PostMapping("/volunteer-session")
    public ResponseEntity<String> createVolunteerSession(@RequestBody volunteerDTO volunteer) {
        try {
            certificateService.createVolunteerSession(volunteer);
            return ResponseEntity.ok("Volunteer session created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    // 세션 유저별로 불러오기
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ResponseDTO<?>> getSessionsByUserId(@PathVariable String userId) {
        List<VolunteerListDTO> sessions = certificateService.getVolunteerSessionsByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST, sessions));
    }

    // 세션 유저 + 타입별로 불러오기
    @GetMapping("/sessions/{documentType}/{userId}")
    public ResponseEntity<ResponseDTO<?>> getSessionsByUserAndTypeId(@PathVariable String documentType, @PathVariable String userId) {
        List<VolunteerListDTO> sessions = certificateService.getVolunteerSessionsByUserIdAndType(documentType, userId);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST, sessions));
    }

    // 자격증 발급하기
    @PostMapping("/issue/{userId}")
    public ResponseEntity<ResponseDTO<?>> issueCertificate(@PathVariable String userId) throws Exception {
        // UUID 생성
        String certificateId = UUID.randomUUID().toString();
        // 서비스 호출
        CertificateDTO certificateDTO = certificateService.issueCertificate(certificateId, userId);

        // 응답 반환
        return ResponseEntity
                .status(SuccessCode.SUCCESS_ISSUE_CERTIFICATE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_ISSUE_CERTIFICATE, certificateDTO));
    }


    /**
     * 자격증 조회 API
     */
    
    @GetMapping("/certificate/{certificateId}")
    public ResponseEntity<ResponseDTO<?>> getCertificateById(@PathVariable String certificateId) throws Exception {
        CertificateDTO certificate = certificateService.getCertificateById(certificateId);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_CERTIFICATE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_CERTIFICATE, certificate));
    }

    
    @GetMapping("/certificate/userId/{userId}")
    public ResponseEntity<ResponseDTO<?>> getCertificateByUserId(@PathVariable String userId) {
        CertificateDTO certificate = certificateService.getCertificateByUserId(userId);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_CERTIFICATE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_CERTIFICATE, certificate));
    }

    @GetMapping("/total-volunteer-hours")
    public ResponseEntity<Integer> getTotalVolunteerHours(@RequestParam String userId) {
        try {
            int totalHours = certificateService.calculateTotalVolunteerHours(userId);
            return ResponseEntity.ok(totalHours);
        } catch (Exception e) {
            log.error("Error calculating total volunteer hours: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
