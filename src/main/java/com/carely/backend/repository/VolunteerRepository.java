package com.carely.backend.repository;


import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    @Query("SELECT DISTINCT v FROM Volunteer v WHERE " +
            "(v.volunteer = :volunteer AND v.caregiver = :caregiver) " +
            "OR " +
            "(v.volunteer = :caregiver AND v.caregiver = :volunteer) " +
            "ORDER BY v.id DESC")
    List<Volunteer> findByVolunteerAndCaregiver(@Param("volunteer") User volunteer, @Param("caregiver") User caregiver);


    List<Volunteer> findByVolunteer(User volunteer);

    List<Volunteer> findByCaregiver(User caregiver);

    @Query("SELECT DISTINCT v FROM Volunteer v WHERE (v.volunteer = :volunteer OR v.caregiver = :volunteer) ORDER BY v.id DESC")
    List<Volunteer> findByVolunteerOrCaregiver(@Param("volunteer") User volunteer);

    List<Volunteer> findByVolunteerAndHasMemoFalse(User writer);

//    @Query("SELECT v FROM Volunteer v " +
//            "WHERE v.caregiver IN (SELECT j.user FROM JoinGroup j WHERE j.group.id = :groupId) " +
//            "AND v.volunteer IN (SELECT j.user FROM JoinGroup j WHERE j.group.id = :groupId)")
//    List<Volunteer> findVolunteersInGroup(@Param("groupId") Long groupId);
}
