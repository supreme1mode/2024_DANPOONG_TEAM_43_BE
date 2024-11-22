package com.carely.backend.service;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.guestBook.ResponseGuestBookDTO;
import com.carely.backend.exception.AlreadyExistsGuestBookException;
import com.carely.backend.exception.NotWriterException;
import com.carely.backend.exception.UserNotFoundException;
import com.carely.backend.exception.VolunteerNotFoundException;
import com.carely.backend.repository.GuestBookRepository;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestBookService {
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final GuestBookRepository guestBookRepository;

    @Transactional
    public void registerGuestBook(RequestGuestBookDTO requestGuestBookDTO, @Valid String user, Long volunteerId) {
        User user_volunteer = userRepository.findByKakaoId(user)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new VolunteerNotFoundException("자원봉사 요청을 찾을 수 없습니다."));

        if (volunteer.getHasGuestBook()) {
            throw new AlreadyExistsGuestBookException("이미 방명록이 존재합니다.");
        }

        guestBookRepository.save(GuestBookEntity.builder()
                .content(requestGuestBookDTO.getContent())
                .volunteerSection(volunteer)
                .caregiver(volunteer.getCaregiver())
                .volunteer(volunteer.getVolunteer())
                .build());


        volunteer.updateVolunteerGuestBook();

    }
    @Transactional
    public List<ResponseGuestBookDTO> getAllGuestBook(String user) {
        // 유저 조회
        User user_volunteer = userRepository.findByKakaoId(user)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        List<Volunteer> volunteers = volunteerRepository.findByVolunteerOrCaregiver(user_volunteer);
        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();
        for (Volunteer volunteer : volunteers) {
            if (volunteer.getHasGuestBook()) {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId())));
            }
            else if (volunteer.getCaregiver().equals(user_volunteer)) {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
                        .caregiverName(volunteer.getCaregiver().getUsername())
                        .volunteerName(volunteer.getVolunteer().getUsername())
                        .caregiverAge(volunteer.getCaregiver().getAge())
                        .caregiverAddress(volunteer.getCaregiver().getAddress())
                        .careDate(volunteer.getDate().toString())
                        .content("작성된 방명록이 없습니다.")
                        .sectionId(volunteer.getId())
                        .durationHours(volunteer.getDurationHours())
                        .build());
            }
            else {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
                        .caregiverName(volunteer.getCaregiver().getUsername())
                        .volunteerName(volunteer.getVolunteer().getUsername())
                        .caregiverAge(volunteer.getCaregiver().getAge())
                        .caregiverAddress(volunteer.getCaregiver().getAddress())
                        .careDate(volunteer.getDate().toString())
                        .content("방명록을 작성해보세요!")
                        .sectionId(volunteer.getId())
                        .durationHours(volunteer.getDurationHours())
                        .build());
            }
        }
        return responseGuestBookDTOList;
    }


    public List<ResponseGuestBookDTO> getGuestBookMyHome(String user) {
        User user_volunteer = userRepository.findByKakaoId(user)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        List<Volunteer> volunteerList = volunteerRepository.findByCaregiver(user_volunteer);
        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();

        for (Volunteer volunteer: volunteerList) {
            if (volunteer.getCaregiver().equals(user_volunteer) && volunteer.getHasGuestBook()) {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId())));
            } else {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
                        .caregiverName(volunteer.getCaregiver().getUsername())
                        .volunteerName(volunteer.getVolunteer().getUsername())
                        .caregiverAge(volunteer.getCaregiver().getAge())
                        .caregiverAddress(volunteer.getCaregiver().getAddress())
                        .careDate(volunteer.getDate().toString())
                        .content("작성된 방명록이 없습니다.")
                        .sectionId(volunteer.getId())
                        .durationHours(volunteer.getDurationHours())
                        .build());
            }
        }
        return responseGuestBookDTOList;
    }

    public List<ResponseGuestBookDTO> getGuestBookCaregiverHome(String user) {
        User user_volunteer = userRepository.findByKakaoId(user)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        List<Volunteer> volunteerList = volunteerRepository.findByVolunteer(user_volunteer);
        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();

        for (Volunteer volunteer: volunteerList) {
            if (volunteer.getVolunteer().equals(user_volunteer) && volunteer.getHasGuestBook()) {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId())));
            } else {
                responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
                        .caregiverName(volunteer.getCaregiver().getUsername())
                        .volunteerName(volunteer.getVolunteer().getUsername())
                        .caregiverAge(volunteer.getCaregiver().getAge())
                        .caregiverAddress(volunteer.getCaregiver().getAddress())
                        .careDate(volunteer.getDate().toString())
                        .content("방명록을 작성해보세요!")
                        .sectionId(volunteer.getId())
                        .durationHours(volunteer.getDurationHours())
                        .build());
            }
        }
        return responseGuestBookDTOList;

    }

    public void deleteGuestBook(String username, Long id) {
        User user_volunteer = userRepository.findByKakaoId(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Volunteer volunteer = volunteerRepository.findById(id).orElseThrow(() -> new VolunteerNotFoundException("존재하지 않음."));
        if (volunteer.getVolunteer().equals(user_volunteer)) {
            volunteer.deleteVolunteerGuestBook();
            guestBookRepository.deleteByVolunteerSectionId(id);
        }
        else {
            throw new NotWriterException("작성자가 아닌 글은 지울 수 없습니다.");
        }
        
        

    }
}
