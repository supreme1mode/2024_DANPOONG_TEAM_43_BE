package com.carely.backend.service;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.guestBook.ResponseGroupGuestbookDTO;
import com.carely.backend.dto.guestBook.ResponseGuestBookDTO;
import com.carely.backend.exception.*;
import com.carely.backend.repository.GroupRepository;
import com.carely.backend.repository.GuestBookRepository;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestBookService {
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final GuestBookRepository guestBookRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void registerGuestBook(RequestGuestBookDTO requestGuestBookDTO, @Valid String kakaoId, Long volunteerId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new VolunteerNotFoundException("자원봉사 요청을 찾을 수 없습니다."));

        // 이미 자원봉사자나 요양보호사의 방명록이 있는 경우
        if ((user.getUserType().equals(UserType.CARE_WORKER)) || (user.getUserType().equals(UserType.VOLUNTEER))) {
            if (volunteer.getCheckVolunteerWriteGuestBook()) {
                throw new AlreadyExistsGuestBookException("이미 방명록이 존재합니다.");
            }
            else {
                volunteer.updateVolunteerGuestBook();
            }
        }
        else {
            if (volunteer.getCheckCaregiverWriteGuestBook()) {
                throw new AlreadyExistsGuestBookException("이미 방명록이 존재합니다.");
            }
            else {
                volunteer.updateCaregiverGuestBook();
            }
        }

        guestBookRepository.save(GuestBookEntity.builder()
                .content(requestGuestBookDTO.getContent())
                .volunteerSection(volunteer)
                .writerType(user.getUserType().name())
                .build());
    }


    // 마이페이지 방명록 리스트 받아오기
    public List<ResponseGroupGuestbookDTO> getMyPageGuestBook(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        List<Volunteer> list = volunteerRepository.findByVolunteerOrCaregiver(user);
        if (list.isEmpty()) {
            throw new ListEmptyException("없음");
        }
        return list.stream().map((this::getGuestbook)).collect(Collectors.toList());
    }

    // 그룹 방명록 리스트 받아오기
    public List<ResponseGroupGuestbookDTO> getGroupGuestbook(Long groupId, String kakaoId) {
        // 해당 그룹 봉사 리스트 받아오기
        Set<Volunteer> list = groupRepository.getReferenceById(groupId).getVolunteerSessions();
        if (list.isEmpty()) {
            throw new ListEmptyException("없음");

        }
        return list.stream().map((this::getGuestbook)).collect(Collectors.toList());
    }


    // 돌려주는 REsponseDTO 만드는 메서드
    public ResponseGroupGuestbookDTO getGuestbook(Volunteer volunteerSessions) {
        String v_Content;
        String c_Content;

        GuestBookEntity volunteer_guestBook = guestBookRepository.findByVolunteerSectionIdAndWriterType(volunteerSessions.getId(), volunteerSessions.getVolunteer().getUserType().name());
        if (volunteer_guestBook == null) {
            v_Content = null;
        } else {
            v_Content = volunteer_guestBook.getContent();
        }
        GuestBookEntity caregiver_guestBook = guestBookRepository.findByVolunteerSectionIdAndWriterType(volunteerSessions.getId(), volunteerSessions.getCaregiver().getUserType().name());
        if (caregiver_guestBook == null) {
            c_Content = null;
        } else {
            c_Content = caregiver_guestBook.getContent();
        }
        return ResponseGroupGuestbookDTO.builder()
                .otherType(ResponseGroupGuestbookDTO.GuestBookDTO.builder()
                        .userType(volunteerSessions.getVolunteer().getUserType().name())
                        .username(volunteerSessions.getVolunteer().getUsername())
                        .content(v_Content)
                        .userId(volunteerSessions.getVolunteer().getId())
                        .build())
                .caregiver(ResponseGroupGuestbookDTO.GuestBookDTO.builder()
                        .userType(volunteerSessions.getCaregiver().getUserType().name())
                        .username(volunteerSessions.getCaregiver().getUsername())
                        .userId(volunteerSessions.getCaregiver().getId())
                        .content(c_Content)
                        .build())
                .build();
    }



//    public void getMyHomeGuestBook(Long userId) {
//        if
//
//
//    }


//
//
//
//
//    @Transactional
//    public List<ResponseGuestBookDTO> getAllGuestBook(String user) {
//        // 유저 조회
//        User user_volunteer = userRepository.findByKakaoId(user)
//                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
//
//        List<Volunteer> volunteers = volunteerRepository.findByVolunteerOrCaregiver(user_volunteer);
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();
//        for (Volunteer volunteer : volunteers) {
//            String writer;
//            String home;
//            String profileName;
//            UserType userType;
//            Long userId;
//            if (volunteer.getVolunteer().equals(user_volunteer)) {
//                home = volunteer.getCaregiver().getUsername() + "님의 집";
//                writer = "내가 남긴 방명록";
//                profileName = volunteer.getCaregiver().getUsername();
//                userType = volunteer.getCaregiver().getUserType();
//                userId = volunteer.getCaregiver().getId();
//            } else {
//                home = "내 집";
//                writer = volunteer.getVolunteer().getUsername() + "님의 방명록";
//                profileName = volunteer.getVolunteer().getUsername();
//                userType = volunteer.getVolunteer().getUserType();
//                userId = volunteer.getVolunteer().getId();
//            }
//                if (volunteer.getHasGuestBook()) {
//                    responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId()).orElseThrow(() -> new GuestBookNotFoundException("방명록 없음")), home, writer, profileName, userType, userId));
//                } else if (volunteer.getCaregiver().equals(user_volunteer)) {
//                    continue;
//                } else {
//                    responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
//                            .home(home)
//                            .writer(writer)
//                            .profileName(profileName)
//                            .userType(userType)
//                            .userId(userId)
//                            .careDate(volunteer.getDate().toString())
//                            .content(null)
//                            .sectionId(volunteer.getId())
//                            .durationHours(volunteer.getDurationHours())
//                            .build());
//                }
//            }
//            return responseGuestBookDTOList;
//        }
//
//
//    public List<ResponseGuestBookDTO> getGuestBookMyHome(String user) {
//        User user_volunteer = userRepository.findByKakaoId(user)
//                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
//
//        List<Volunteer> volunteerList = volunteerRepository.findByCaregiver(user_volunteer);
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();
//
//        for (Volunteer volunteer: volunteerList) {
//                String writer;
//                String home;
//                String profileName;
//                UserType userType;
//                Long userId;
//                if (volunteer.getVolunteer().equals(user_volunteer)) {
//                    home = volunteer.getCaregiver().getUsername()+ "님의 집";
//                    writer = "내가 남긴 방명록";
//                    profileName = volunteer.getCaregiver().getUsername();
//                    userType = volunteer.getCaregiver().getUserType();
//                    userId = volunteer.getCaregiver().getId();
//                }
//                else {
//                    home = "내 집";
//                    writer = volunteer.getVolunteer().getUsername()+"님의 방명록";
//                    profileName = volunteer.getVolunteer().getUsername();
//                    userType = volunteer.getVolunteer().getUserType();
//                    userId = volunteer.getVolunteer().getId();
//                }
//            if (volunteer.getCaregiver().equals(user_volunteer) && volunteer.getHasGuestBook()) {
//                responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId()).orElseThrow(() -> new GuestBookNotFoundException("방명록 없음")), home, writer, profileName, userType, userId));
//            }
//        }
//        return responseGuestBookDTOList;
//    }
//
//    public List<ResponseGuestBookDTO> getGuestBookCaregiverHome(String user) {
//        User user_volunteer = userRepository.findByKakaoId(user)
//                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
//
//        List<Volunteer> volunteerList = volunteerRepository.findByVolunteer(user_volunteer);
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = new ArrayList<>();
//
//        for (Volunteer volunteer: volunteerList) {
//            String writer;
//            String home;
//            String profileName;
//            UserType userType;
//            Long userId;
//            if (volunteer.getVolunteer().equals(user_volunteer)) {
//                home = volunteer.getCaregiver().getUsername()+ "님의 집";
//                writer = "내가 남긴 방명록";
//                profileName = volunteer.getCaregiver().getUsername();
//                userType = volunteer.getCaregiver().getUserType();
//                userId = volunteer.getCaregiver().getId();
//            }
//            else {
//                home = "내 집";
//                writer = volunteer.getVolunteer().getUsername()+"님의 방명록";
//                profileName = volunteer.getVolunteer().getUsername();
//                userType = volunteer.getVolunteer().getUserType();
//                userId = volunteer.getVolunteer().getId();
//            }
//
//            if (volunteer.getVolunteer().equals(user_volunteer) && volunteer.getHasGuestBook()) {
//                responseGuestBookDTOList.add(ResponseGuestBookDTO.entityToDto(guestBookRepository.findByVolunteerSectionId(volunteer.getId()).orElseThrow(() -> new GuestBookNotFoundException("방명록 없음")), home, writer, profileName, userType, userId));
//            } else {
//                responseGuestBookDTOList.add(ResponseGuestBookDTO.builder()
//                        .home(home)
//                        .writer(writer)
//                        .profileName(profileName)
//                        .userType(userType)
//                        .userId(userId)
//                        .careDate(volunteer.getDate().toString())
//                        .content(null)
//                        .sectionId(volunteer.getId())
//                        .durationHours(volunteer.getDurationHours())
//                        .build());
//            }
//        }
//        return responseGuestBookDTOList;
//
//    }

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
