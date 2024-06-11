package com.motherlove.repositories;

import com.motherlove.models.entities.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long>{
    @Query(value = "SELECT ph FROM PaymentHistory ph where ph.order.orderId=:orderId")
    PaymentHistory findPaymentHistoryByOrderId(Long orderId);
}
