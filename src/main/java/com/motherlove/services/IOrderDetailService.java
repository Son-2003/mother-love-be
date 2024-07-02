package com.motherlove.services;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.OrderDetail;
import com.motherlove.models.payload.requestModel.CartItem;

import java.util.List;

public interface IOrderDetailService {
    List<OrderDetail> createOrderDetails(List<CartItem> cartItems, Order order);
}
