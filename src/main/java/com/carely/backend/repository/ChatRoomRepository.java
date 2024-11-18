package com.carely.backend.repository;

import com.carely.backend.domain.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByRoomId(String roomId);
    // Optional<ChatRoomEntity> findByName(String name);
    @Query("SELECT c FROM ChatRoomEntity c WHERE " +
            "(c.user1 = :user1 AND c.user2 = :user2) OR " +
            "(c.user1 = :user2 AND c.user2 = :user1)")
    Optional<ChatRoomEntity> findByUsers(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query("SELECT c FROM ChatRoomEntity c WHERE c.user1 = :viewerId OR c.user2 = :viewerId")
    List<ChatRoomEntity> findByUser1OrUser2(@Param("viewerId") Long viewerId);
}
