package com.comdao.api.notices;

import com.comdao.api.notices.entities.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticesRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByUser_IdOrUser_IdIsNullOrderByCreatedAsc(Long userId, Pageable of);
}
