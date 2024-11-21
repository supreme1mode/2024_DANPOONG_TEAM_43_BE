package com.carely.backend.dto.group;
import com.carely.backend.domain.Group;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class GetGroupDTO {
    private Long groupId;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        private Long groupId;
        private String groupName;
        private String city;
        private String description;
        private Integer headCount; // 인원수
        private UserType userType;
        private Boolean isLiked;
        private Boolean isWriter;
        // private String groupImage;

        public static List toDTO(Group e, Boolean isLiked, String userId) {
            Boolean isWriter = e.getOwnerId().equals(userId);

            return List.builder()
                    .groupId(e.getId())
                    .groupName(e.getGroupName())
                    .city(e.getCity())
                    .description(e.getDescription())
                    .headCount(e.getJoinGroups() != null ? e.getJoinGroups().size() : 0) // JoinGroup을 사용한 인원수 계산
                    .userType(e.getUserType())
                    .isLiked(isLiked)
                    .isWriter(isWriter)
                   //  .groupImage(e.getGroupImage())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private Long groupId;
        private String groupName;
        private String city;
        private String description;
        private String schedule;
        private Integer headCount; // 인원수
        private Boolean isLiked;
        private Boolean isWriter;
        // private String groupImage;

        public static Detail toDTO(Group e, Boolean isLiked, String userId) {
            Boolean isWriter = e.getOwnerId().equals(userId);


            return Detail.builder()
                    .groupId(e.getId())
                    .groupName(e.getGroupName())
                    .city(e.getCity())
                    .description(e.getDescription())
                    .schedule(e.getSchedule())
                    .headCount(e.getJoinGroups() != null ? e.getJoinGroups().size() : 0) // JoinGroup을 사용한 인원수 계산
                    .isLiked(isLiked)
                    .isWriter(isWriter)
                 //    .groupImage(e.getGroupImage())
                    .build();
        }
    }
}