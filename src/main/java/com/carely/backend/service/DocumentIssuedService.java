package com.carely.backend.service;


import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.dto.certificate.VolunteerListDTO;
import com.carely.backend.dto.document.ResponseDocumentDTO;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import com.carely.backend.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentIssuedService {
    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;



    public List<ResponseDocumentDTO> getDocumentIssuedList(String kakaoId, String documentType) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<VolunteerListDTO> list= certificateService.getVolunteerSessionsByUserIdAndType(documentType, kakaoId);
        return list.stream().map((volunteerListDTO -> {
            Volunteer volunteer = volunteerRepository.getReferenceById(Long.valueOf(volunteerListDTO.getVolunteerSessionsId()));
            return ResponseDocumentDTO.builder()
                    .myIdentity(user.getIdentity())
                    .myType(user.getUserType().name())
                    .myName(user.getUsername())
                    .address(user.getAddress())
                    .partnerName(volunteer.getCaregiver().getUsername())
                    .partnerType(volunteer.getCaregiver().getUserType().name())
                    .partnerId(volunteer.getCaregiver().getId())
                    .volunteerDate(volunteer.getDate().toString())
                    .durationTimes(volunteer.getDurationHours())
                    .content(volunteer.getMainTask())
                    .volunteerSessionType(volunteerListDTO.getVolunteerType())
                    .build();
        })).collect(Collectors.toList());

    }
}
