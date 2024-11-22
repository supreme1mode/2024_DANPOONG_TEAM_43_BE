package com.carely.backend.repository;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBookEntity, Long> {
    //@Query("SELECT g FROM GuestBookEntity g WHERE g.id = :id")
    GuestBookEntity findByVolunteerId(Long volunteerId);
    GuestBookEntity findByVolunteerSectionId(Long volunteerSectionId);
    List<GuestBookEntity> findByCaregiver(User caregiver);
    List<GuestBookEntity> findByVolunteer(User volunteer);

    void deleteByVolunteerSectionId(Long volunteerSectionId);


}
