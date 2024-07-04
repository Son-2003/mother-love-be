package com.motherlove.repositories;

import com.motherlove.models.entities.Order;
import com.motherlove.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);
    List<Order> findByStatusOrderByOrderDate(OrderStatus orderStatus);
}
