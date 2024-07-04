package com.motherlove.models.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionDto {
    private Long stockTransactionId;
    private LocalDateTime stockTransactionDate;
    private int quantity;
    private float totalPrice;
    private SupplierDto supplier;
    private ProductDto product;
}
