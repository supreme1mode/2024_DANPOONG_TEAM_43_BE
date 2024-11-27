package com.carely.backend.repository;

import com.carely.backend.domain.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
}
