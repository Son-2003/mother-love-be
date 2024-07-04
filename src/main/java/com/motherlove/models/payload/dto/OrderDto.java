package com.motherlove.models.payload.dto;

import com.motherlove.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private boolean isFeedBack;
    private float totalAmount;
    private float afterTotalAmount;
}
