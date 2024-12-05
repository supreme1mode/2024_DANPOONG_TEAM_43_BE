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

    @Query("SELECT DISTINCT g1.group FROM JoinGroup g1 JOIN JoinGroup g2 " +
            "ON g1.group = g2.group " +
            "WHERE g1.user = :user1 AND g2.user = :user2")
    List<Group> findCommonGroups(@Param("user1") User user1, @Param("user2") User user2);

    boolean existsByUserAndGroup(User user, Group group);
}