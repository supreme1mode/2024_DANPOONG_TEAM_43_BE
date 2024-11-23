package com.carely.backend.controller;

import com.carely.backend.domain.Volunteer;
import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.service.CertificateService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}