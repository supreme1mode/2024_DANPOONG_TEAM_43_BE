package com.carely.backend.repository;

import com.carely.backend.domain.Group;
import com.carely.backend.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT MAX(n.createdAt) FROM News n WHERE n.group.id = :groupId")
    LocalDateTime findLastNewsTimeByGroupId(@Param("groupId") Long groupId);

    List<News> findAllByGroupOrderByCreatedAtDesc(Group group);
}
