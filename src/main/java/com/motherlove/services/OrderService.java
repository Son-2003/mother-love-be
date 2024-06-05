package com.motherlove.services;

import com.motherlove.models.payload.responseModel.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
}
