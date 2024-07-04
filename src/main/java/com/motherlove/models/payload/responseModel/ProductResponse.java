package com.motherlove.models.payload.responseModel;

import com.motherlove.models.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private float price;
    private int quantityProduct;
    private ProductStatus status;
    private String image;
}
