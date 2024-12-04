package com.carely.backend.repository;

import com.carely.backend.domain.Group;
import com.carely.backend.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByCity(String city);
}
