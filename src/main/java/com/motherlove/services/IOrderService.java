package com.motherlove.services;

import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    OrderResponse getOrderDetail(Long orderId);
    OrderResponse createOrder(List<CartItem> cartItems, Long userId, Long addressId, Long voucherId);
    Page<OrderResponse> searchOrder(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams);
    Page<OrderResponse> searchOrderUser(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams, Long userId);
}
