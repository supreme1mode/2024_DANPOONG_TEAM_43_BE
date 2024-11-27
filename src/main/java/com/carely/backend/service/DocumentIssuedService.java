package com.carely.backend.service;


import com.carely.backend.domain.User;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentIssuedService {
    public final CertificateService certificateService;
    public final UserRepository userRepository;


    public void getDocumentIssuedList(String username, String documentType) {
        User user = userRepository.findByKakaoId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<VolunteerListDTO> list= certificateService.getVolunteerSessionsByUserIdAndType(documentType, user.getId().toString());


    }
}
