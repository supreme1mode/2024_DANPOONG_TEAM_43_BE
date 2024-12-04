package com.carely.backend.repository;


import com.carely.backend.domain.Group;
import com.carely.backend.domain.JoinGroup;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JoinGroupRepository extends JpaRepository<JoinGroup, Long> {
    boolean existsByGroupAndUser(Group group, User superUser);
    @Modifying
    @Transactional
    @Query("DELETE FROM JoinGroup jg WHERE jg.group = :group AND jg.user = :user")
    void deleteByGroupAndUser(Group group, User user);


    @Query("SELECT g FROM JoinGroup g JOIN g.user u1 JOIN g.user u2 WHERE u1.id = :userId1 AND u2.id = :userId2")
    List<Group> findCommonGroups(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}