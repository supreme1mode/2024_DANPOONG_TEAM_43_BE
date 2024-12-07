package com.carely.backend.dto.group;


import com.carely.backend.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CreateGroupDTO {
    private String groupName;
    private String city;
    private String description;
    private String schedule;
    // private UserType userType;


    public static Group toEntity(CreateGroupDTO dto, String kakaoId) {
        return Group.builder()
                .groupName(dto.getGroupName())
                .city(dto.getCity())
                .description(dto.getDescription())
                .schedule(dto.getSchedule())
                // .userType(dto.getUserType())
                // .groupImage(url)
                .ownerId(kakaoId)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long groupId;
        private String groupName;
        private String city;
        private String description;
        private String schedule;
        private Integer headCount;
        // private UserType userType;
        // private String groupImage;

        public static Res toDTO(Group e) {
            return Res.builder()
                    .groupId(e.getId())
                    .groupName(e.getGroupName())
                    .city(e.getCity())
                    .description(e.getDescription())
                    .schedule(e.getSchedule())
                    .headCount(e.getJoinGroups() != null ? e.getJoinGroups().size() : 0) // JoinGroup을 사용한 인원수 계산
                 //   .userType(e.getUserType())
                //     .groupImage(e.getGroupImage())
                    .build();
        }
    }
}
