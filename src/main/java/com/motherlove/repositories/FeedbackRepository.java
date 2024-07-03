package com.motherlove.repositories;

import com.motherlove.models.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
    List<Feedback> findByProduct_ProductId(Long productId);
    List<Feedback> findByOrder_OrderId(Long orderId);
}
