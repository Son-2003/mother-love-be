package com.motherlove.models.payload.responseModel;

import com.motherlove.models.enums.PaymentHistoryStatus;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.PaymentMethodDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPaymentHistoryResponse {
    private Long paymentHistoryId;
    private float amount;
    private PaymentHistoryStatus status;
    private LocalDateTime createdDate;
    private PaymentMethodDto paymentMethod;
    private OrderDto order;
}
