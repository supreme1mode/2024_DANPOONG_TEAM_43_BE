package com.carely.backend.repository;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByKakaoId(String kakaoId);
    Optional<User> findByKakaoId(String kakaoId);
    List<User> findByCity(String city);
    List<User> findByCityAndUserTypeIn(String city, List<UserType> userTypes);

    List<User> findByUserTypeIn(List<UserType> userTypes);
}
