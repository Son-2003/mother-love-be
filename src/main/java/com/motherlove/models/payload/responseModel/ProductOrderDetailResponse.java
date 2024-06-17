package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderDetailResponse {
    private Long productId;
    private String productName;
    private String description;
    private float price;
    private int quantity;
    private int status;
    private String image;
    private ProductDto giftResponse;
}
