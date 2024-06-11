package com.motherlove.models.payload.dto;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryDto {
    private Long paymentHistoryId;

    private float amount;

    private int status;

    private LocalDateTime createdDate;

    private Long paymentMethodId;

    private Long orderId;
}
