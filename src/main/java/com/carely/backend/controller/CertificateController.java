package com.carely.backend.controller;

import com.carely.backend.domain.User;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;
    private final UserRepository userRepository;

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

    @GetMapping("/api/sessions/{userId}")
    public ResponseEntity<List<VolunteerListDTO>> getSessionsByUserId(@PathVariable String userId) {
        try {
            List<VolunteerListDTO> sessions = certificateService.getVolunteerSessionsByUserId(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonList((VolunteerListDTO) Map.of("error", e.getMessage())));
        }
    }

    @PostMapping("/issue/{userId}")
    public ResponseEntity<String> issueCertificate(@PathVariable Long userId) throws Exception {
        // UUID 생성
        String certificateId = UUID.randomUUID().toString();

        // username을 userRepository에서 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found for userId: " + userId);
        }
        String username = userOptional.get().getUsername();

        // 현재 날짜 생성
        String issueDate = LocalDate.now().toString();

        // 서비스 호출
        String transactionHash = certificateService.issueCertificate(certificateId, userId, username, issueDate);

        // 응답 반환
        return ResponseEntity.ok("Certificate issued successfully. Transaction Hash: " + transactionHash);
    }


    /**
     * 자격증 조회 API
     */
    @GetMapping("/certificate/{certificateId}")
    public ResponseEntity<CertificateDTO> getCertificateById(@PathVariable String certificateId) {
        try {
            ResponseEntity certificate = certificateService.getCertificateById(certificateId);
            return certificate;
        } catch (Exception e) {
            log.error("Error fetching certificate: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/userId/{userId}")
    public ResponseEntity<CertificateDTO> getCertificateByUserId(@PathVariable String userId) {
        ResponseEntity certificate = certificateService.getCertificateByUserId(userId);
        return certificate;
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