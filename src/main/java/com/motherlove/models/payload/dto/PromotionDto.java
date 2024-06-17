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
public class PromotionDto {
    private Long promotionId;
    private String promotionName;
    private String description;
    private int quantityOfGift;
    private int availableQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ProductDto product;
    private ProductDto gift;
}
