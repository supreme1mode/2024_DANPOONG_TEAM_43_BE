package com.carely.backend.repository;

import com.carely.backend.domain.GuestBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBookEntity, Long> {
    //@Query("SELECT g FROM GuestBookEntity g WHERE g.id = :id")
    //Optional<GuestBookEntity> findByVolunteerSectionId(Long volunteerSectionId);

    void deleteByVolunteerSectionId(Long volunteerSectionId);

    GuestBookEntity findByVolunteerSectionIdAndWriterType(Long volunteerSectionId, String writerType);

    GuestBookEntity findByVolunteerSectionIdAndWriterId(Long volunteerSectionId, Long writerId);

}
