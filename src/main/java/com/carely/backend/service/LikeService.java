package com.carely.backend.service;


import com.carely.backend.domain.Group;
import com.carely.backend.domain.Like;
import com.carely.backend.domain.User;
import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.exception.AlreadyLikeGroupException;
import com.carely.backend.exception.GroupNotFoundException;
import com.carely.backend.exception.NotLikeGroupUserException;
import com.carely.backend.repository.GroupRepository;
import com.carely.backend.repository.LikeRepository;
import com.carely.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public LikeService(GroupRepository groupRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public void likeAddGroup(String kakaoId, Long groupId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        // 이미 좋아요가 있는지 확인
        if (likeRepository.existsByUserAndGroup(user, group)) {
            throw new AlreadyLikeGroupException("이미 찜한 그룹입니다.");
        }

        // 새로운 좋아요 엔티티 생성 및 저장
        Like like = Like.builder()
                .user(user)
                .group(group)
                .build();
        likeRepository.save(like);
    }

    @Transactional
    public void likeRemoveGroup(String kakaoId, Long groupId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("그룹을 찾을 수 없습니다."));

        // 좋아요 관계가 존재하는지 확인하고 삭제
        Like like = likeRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new NotLikeGroupUserException("찜한 그룹이 아닙니다."));

        likeRepository.delete(like);
    }

    public List<GetGroupDTO.List> getAllLikedGroups(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<Group> likedGroups = likeRepository.findAllByUser(user).stream()
                .map(Like::getGroup)
                .collect(Collectors.toList());

        return likedGroups.stream()
                .map(group -> GetGroupDTO.List.toDTO(group, true, kakaoId))
                .collect(Collectors.toList());
    }

}

