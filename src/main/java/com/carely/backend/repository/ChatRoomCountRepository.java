package com.carely.backend.repository;

import com.carely.backend.domain.ChatRoomCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomCountRepository extends JpaRepository<ChatRoomCountEntity, Long> {
}
