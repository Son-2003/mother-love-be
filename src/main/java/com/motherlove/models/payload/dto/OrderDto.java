package com.motherlove.models.payload.dto;

import com.motherlove.models.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private boolean isFeedBack;
    private float totalAmount;
    private float afterTotalAmount;
}
