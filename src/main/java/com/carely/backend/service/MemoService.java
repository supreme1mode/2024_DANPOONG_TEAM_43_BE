package com.carely.backend.service;


import com.carely.backend.domain.Memo;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.memo.MemoResponseDTO;
import com.carely.backend.dto.volunteer.GetVolunteerInfoDTO;
import com.carely.backend.exception.AlreadyExistsMemoException;
import com.carely.backend.exception.UserMustNotCaregiverException;
import com.carely.backend.exception.VolunteerNotFoundException;
import com.carely.backend.repository.MemoRepository;
import com.carely.backend.repository.UserRepository;
import com.carely.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final VolunteerRepository volunteerRepository;

    @Transactional
    public MemoResponseDTO.List createMemo(String kakaoId, String content, Long volunteerId) {
        User writer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new VolunteerNotFoundException("자원봉사 요청을 찾을 수 없습니다."));

        if (volunteer.getHasMemo()) {
            throw new AlreadyExistsMemoException("이미 메모가 존재합니다.");
        }

        User receiver = userRepository.findById(volunteer.getCaregiver().getId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String recentAiSummary = receiver.getAiSummary() == null ? "" : receiver.getAiSummary();

        Map<String, String> aiSummaryMap = openAIService.getSummary(content, recentAiSummary);
        String aiSummary = aiSummaryMap.get("all");
        receiver.updateAiSummary(aiSummary);

        Memo memo = Memo.builder()
                .content(content)
                .writer(writer)
                .receiver(receiver)
                .all(aiSummaryMap.get("all"))
                .healthy(aiSummaryMap.get("healthy"))
                .additionalHealth(aiSummaryMap.get("additionalHealth"))
                .social(aiSummaryMap.get("social"))
                .eat(aiSummaryMap.get("eat"))
                .voiding(aiSummaryMap.get("voiding"))
                .build();

        Memo newMemo = memoRepository.save(memo);
        volunteer.updateVolunteerMemo();

        return MemoResponseDTO.List.toDTO(newMemo);
    }

    public List<MemoResponseDTO.List> getMemoList(String kakaoId, Long userId) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<Memo> memos = memoRepository.findByReceiver(receiver);

        return memos.stream()
                .map(MemoResponseDTO.List::toDTO)
                .collect(Collectors.toList());
    }

    public List<GetVolunteerInfoDTO> getVolunteerInfo(String kakaoId) {
        User writer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (writer.getUserType() == UserType.CAREGIVER) {
            throw new UserMustNotCaregiverException("");
        }

        List<Volunteer> volunteers = volunteerRepository.findByVolunteerAndHasMemoFalse(writer);

        return volunteers.stream()
                .map(volunteer -> {
                    // Caregiver 조회
                    User receiver = volunteer.getCaregiver();

                    // Memo 리스트 조회 및 최신 Memo의 getAll() 값 가져오기
                    String memoAll = receiver.getMemos().stream() // receiver의 Memo 리스트
                            .sorted(Comparator.comparing(Memo::getCreatedAt).reversed()) // 최신순 정렬
                            .findFirst() // 가장 최근 Memo 선택
                            .map(Memo::getAll) // getAll() 값 가져오기
                            .orElse(""); // Memo가 없으면 ""

                    // DTO 생성
                    return GetVolunteerInfoDTO.toDTO(
                            volunteer,
                            volunteer.getVolunteer(),
                            receiver,
                            memoAll // 가장 최근 Memo의 getAll() 값 또는 ""
                    );
                })
                .collect(Collectors.toList());

    }


}
