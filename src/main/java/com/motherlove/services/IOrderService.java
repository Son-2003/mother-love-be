package com.motherlove.services;

import com.motherlove.models.entities.Order;
import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    OrderResponse getOrderDetail(Long orderId);
    Order getOrderByOrderId(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse createOrder(List<CartItem> cartItems, Long userId, Long addressId, Long voucherId, boolean isPreOrder);
    Page<OrderResponse> searchOrder(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams, Long userId);
    OrderResponse updateOrderCompleted(Long orderId);
}
