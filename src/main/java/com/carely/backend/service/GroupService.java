package com.carely.backend.service;


import com.carely.backend.domain.*;
import com.carely.backend.dto.group.CreateGroupDTO;
import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.dto.user.UserResponseDTO;
import com.carely.backend.exception.AlreadyInGroupException;
import com.carely.backend.exception.GroupNotFoundException;
import com.carely.backend.exception.NotUserInGroupException;
import com.carely.backend.exception.NotWriterException;
import com.carely.backend.repository.*;
import com.carely.backend.service.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JoinGroupRepository joinGroupRepository;
    private final S3Uploader s3Uploader;
    private final VolunteerRepository volunteerRepository;
    private final LikeRepository likeRepository;
    private final NewsRepository newsRepository;


    public CreateGroupDTO.Res createCaregiverGroup(String kakaoId, CreateGroupDTO groupDTO) throws IOException {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

//        String url = null;
//
//        if (image != null) {
//                try {
//                    url = s3Uploader.upload(image, "group");
//                } catch (IOException e) {
//                    throw new RuntimeException("S3 업로드 실패", e);
//                }
//
//        } else {
//            System.out.println("업로드할 파일이 제공되지 않았습니다.");
//        }

        Group group = CreateGroupDTO.toEntity(groupDTO, kakaoId);
        Group newGroup = groupRepository.save(group);
        return CreateGroupDTO.Res.toDTO(newGroup);
    }

    public List<GetGroupDTO.GroupList> getGroupList(String kakaoId) {
        List<Group> groups = groupRepository.findAll();

        return groups.stream()
                .map(group -> {
                    // 최신 News 시간 조회 (없으면 null 반환)
                    LocalDateTime lastNewsTime = newsRepository.findLastNewsTimeByGroupId(group.getId());

                    // DTO 변환
                    return GetGroupDTO.GroupList.toDTO(group, lastNewsTime);
                })
                .collect(Collectors.toList());
    }

    public GetGroupDTO.Detail getGroupDetail(Long groupId, String kakaoId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        return GetGroupDTO.Detail.toDTO(group);
    }

    @Transactional
    public GetGroupDTO.List joinGroup(String kakaoId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 그룹에 가입되어 있는지 확인
        boolean isAlreadyInGroup = joinGroupRepository.existsByGroupAndUser(group, user);
        if (isAlreadyInGroup) {
            throw new AlreadyInGroupException("이미 가입되어 있는 유저입니다.");
        }

        // JoinGroup 엔티티 생성 및 저장
        JoinGroup joinGroup = JoinGroup.builder()
                .group(group)
                .user(user)
                .build();
        joinGroupRepository.save(joinGroup);

        return GetGroupDTO.List.toDTO(group);
    }

    @Transactional
    public GetGroupDTO.List leaveGroup(String kakaoId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 그룹에 가입되어 있는지 확인
        boolean isMember = joinGroupRepository.existsByGroupAndUser(group, user);

        if (!isMember) {
            throw new NotUserInGroupException("그룹에 가입되어 있지 않은 유저입니다.");
        }

        // JoinGroup 엔티티 삭제
        joinGroupRepository.deleteByGroupAndUser(group, user);

        return GetGroupDTO.List.toDTO(group);
    }

    public void deleteGroup(String kakaoId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        if(group.getOwnerId().equals(kakaoId)) {
            groupRepository.delete(group);
        } else {
            throw new NotWriterException("작성자가 아닙니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<GetGroupDTO.List> getUserJoinedGroups(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        // 유저가 가입한 그룹 목록 조회 및 DTO 변환
        return user.getJoinGroups().stream()
                .map(joinGroup -> {
                    Group group = joinGroup.getGroup();
                    return GetGroupDTO.List.toDTO(group);
                })
                .collect(Collectors.toList());
    }


    public List<UserResponseDTO.Group> getGroupUser(Long groupId, String kakaoId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        User currentUser = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<User> users = group.getJoinGroups().stream()
                .map(JoinGroup::getUser)
                .collect(Collectors.toList());

        return users.stream()
                .map(user -> {
                    Long totalDuration = calculateTotalDurationForGroupUser(user, currentUser);
                    return UserResponseDTO.Group.toDTO(user, totalDuration);
                })
                .collect(Collectors.toList());
    }

    private Long calculateTotalDurationForGroupUser(User user1, User user2) {
        List<Volunteer> sharedVolunteers = volunteerRepository.findByVolunteerAndCaregiver(user1, user2);
        return sharedVolunteers.stream()
                .mapToLong(volunteer -> {
                    if (volunteer.getStartTime() != null && volunteer.getEndTime() != null &&
                            volunteer.getEndTime().isBefore(LocalDateTime.now())) { // 종료 시간이 현재 시간보다 이전인 경우
                        return volunteer.getDurationHours();
                    }
                    return 0;
                })
                .sum();
    }

}
