package com.motherlove.models.payload.dto;

import com.motherlove.models.payload.responseModel.ProductOrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Long orderDetailId;
    private int quantity;
    private float unitPrice;
    private float totalPrice;
    private ProductOrderDetailResponse product;
}
