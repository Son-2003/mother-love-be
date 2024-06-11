package com.motherlove.services;

import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    OrderResponse createOrder(List<CartItem> cartItems, Long userId);
}
