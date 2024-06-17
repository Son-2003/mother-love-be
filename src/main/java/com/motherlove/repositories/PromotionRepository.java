package com.motherlove.repositories;

import com.motherlove.models.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p WHERE p.product.productId = :productId ORDER BY p.createdDate DESC")
    Optional<Promotion> findTopByProductIdOrderByCreatedDateDesc(@Param("productId") Long productId);
}
