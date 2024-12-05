package com.carely.backend.repository;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBookEntity, Long> {
    //@Query("SELECT g FROM GuestBookEntity g WHERE g.id = :id")
    //Optional<GuestBookEntity> findByVolunteerSectionId(Long volunteerSectionId);

    void deleteByVolunteerSessionId(Long volunteerSessionId);

    GuestBookEntity findByVolunteerSessionIdAndWriterType(Long volunteerSessionId, String writerType);

    GuestBookEntity findByVolunteerSessionIdAAndWriterId(Long volunteerSessionId, Long writerId);

}
