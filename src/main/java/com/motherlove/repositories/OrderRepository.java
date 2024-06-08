package com.motherlove.repositories;

import com.motherlove.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserId(Long userId);
    Order findByOrderDate(LocalDateTime orderDate);
}
