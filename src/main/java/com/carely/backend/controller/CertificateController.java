package com.carely.backend.controller;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.service.CertificateService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

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
    public ResponseEntity<List<Map<String, Object>>> getSessionsByUserId(@PathVariable String userId) {
        try {
            List<Map<String, Object>> sessions = certificateService.getVolunteerSessionsByUserId(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }
}