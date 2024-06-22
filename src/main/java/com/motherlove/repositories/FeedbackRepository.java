package com.motherlove.repositories;

import com.motherlove.models.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProduct_ProductId(Long productId);
    List<Feedback> findByOrder_OrderId(Long orderId);
}
