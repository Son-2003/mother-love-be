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
public class PointTransactionDto {
    private Long transactionId;
    private int points;
    private LocalDateTime transactionDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
