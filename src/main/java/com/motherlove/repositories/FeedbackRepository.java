package com.motherlove.repositories;

import com.motherlove.models.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
    Page<Feedback> findByProduct_ProductId(Long productId, Pageable pageable);
    List<Feedback> findByOrder_OrderId(Long orderId);
}
