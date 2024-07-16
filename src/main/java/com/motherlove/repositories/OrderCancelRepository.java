package com.motherlove.repositories;

import com.motherlove.models.entities.OrderCancel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCancelRepository extends JpaRepository<OrderCancel, Long> {
    Page<OrderCancel> findByOrder_User_UserId(Long userId, Pageable pageable);
}
