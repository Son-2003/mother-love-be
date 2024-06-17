package com.motherlove.repositories;

import com.motherlove.models.entities.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    Page<PointTransaction> findAllByUser_UserId(Long userId, Pageable pageable);

}
