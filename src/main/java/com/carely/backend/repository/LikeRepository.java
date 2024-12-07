package com.carely.backend.repository;


import com.carely.backend.domain.Group;
import com.carely.backend.domain.Like;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndGroup(User superUser, Group group);
    boolean existsByGroupAndUser(Group group, User user);

    List<Like> findAllByUser(User user);

    Optional<Like> findByUserAndGroup(User user, Group group);
}