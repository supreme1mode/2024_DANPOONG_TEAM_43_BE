package com.carely.backend.repository;


import com.carely.backend.domain.Group;
import com.carely.backend.domain.JoinGroup;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface JoinGroupRepository extends JpaRepository<JoinGroup, Long> {
    boolean existsByGroupAndUser(Group group, User superUser);
    @Modifying
    @Transactional
    @Query("DELETE FROM JoinGroup jg WHERE jg.group = :group AND jg.user = :user")
    void deleteByGroupAndUser(Group group, User user);
}