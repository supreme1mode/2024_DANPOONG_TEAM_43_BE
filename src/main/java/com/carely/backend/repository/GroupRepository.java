package com.carely.backend.repository;

import com.carely.backend.domain.Group;
import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByCity(String city);

    @Query("SELECT g FROM Group g LEFT JOIN JoinGroup ug ON ug.group = g AND ug.user = :user WHERE ug.group IS NULL")
    List<Group> findGroupsNotJoinedByUser(@Param("user") User user);

}
