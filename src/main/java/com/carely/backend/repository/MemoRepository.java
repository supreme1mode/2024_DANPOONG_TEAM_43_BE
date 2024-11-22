package com.carely.backend.repository;


import com.carely.backend.domain.Memo;
import com.carely.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByReceiver(User receiver);
}
