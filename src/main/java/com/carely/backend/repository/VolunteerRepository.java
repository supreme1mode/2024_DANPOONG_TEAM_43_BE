package com.carely.backend.repository;


import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    @Query("SELECT v FROM Volunteer v WHERE (v.volunteer = :volunteer AND v.caregiver = :caregiver) OR (v.volunteer = :caregiver AND v.caregiver = :volunteer)")
    List<Volunteer> findByVolunteerAndCaregiver(@Param("volunteer") User volunteer, @Param("caregiver") User caregiver);

}
