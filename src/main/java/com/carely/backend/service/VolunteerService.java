package com.carely.backend.service;


import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.dto.volunteer.CreateVolunteerDTO;
import com.carely.backend.dto.volunteer.GetVolunteerInfoDTO;
import com.carely.backend.exception.ChatMessageNotFoundException;
import com.carely.backend.exception.UserNotFoundException;
import com.carely.backend.exception.VolunteerNotFoundException;
import com.carely.backend.repository.ChatMessageRepository;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public CreateVolunteerDTO.Res createVolunteer(CreateVolunteerDTO dto) {
        // 자원봉사자 및 간병인 조회
        User volunteer = userRepository.findById(dto.getVolunteerId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        User caregiver = userRepository.findById(dto.getCaregiverId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // Volunteer 객체 생성
        Volunteer volunteerEntity = Volunteer.builder()
                .date(dto.getStartTime().toLocalDate()) // 요청 날짜는 시작 시간 기준
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .durationHours(dto.getDurationHours())
                .salary(dto.getSalary())
                .location(dto.getLocation())
                .mainTask(dto.getMainTask())
                .volunteerType(dto.getVolunteerType())
                .volunteer(volunteer)
                .roomId(dto.getRoomId())
                .caregiver(caregiver)
                .isApproved(false)
                .build();

        // 저장
        return CreateVolunteerDTO.Res.toDTO(volunteerRepository.save(volunteerEntity));
    }

    @Transactional
    public CreateVolunteerDTO.Res updateApproval(Long volunteerId, Long messageId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new VolunteerNotFoundException("자원봉사 요청을 찾을 수 없습니다."));

        volunteer.updateVolunteerApproval();

        chatMessageRepository.findById(messageId);
        ChatMessageEntity chat = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ChatMessageNotFoundException("채팅 메세지를 찾을 수 없습니다."));

        chat.updateVolunteerApproval();
        chatMessageRepository.save(chat);

        Volunteer savedVolunteer = volunteerRepository.save(volunteer);
        System.out.println(savedVolunteer.getIsApproved());
        return CreateVolunteerDTO.Res.toDTO(savedVolunteer);

    }

    public GetVolunteerInfoDTO getVolunteerInfo(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new VolunteerNotFoundException("자원봉사 요청을 찾을 수 없습니다."));

        return GetVolunteerInfoDTO.toDTO(volunteer, volunteer.getVolunteer());
    }
}
