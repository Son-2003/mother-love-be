package com.motherlove.models.payload.dto;

import com.motherlove.models.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String productName;
    private String description;
    private float price;
    private int quantityProduct;
    private ProductStatus status;
    private String image;
    private CategoryDto category;
    private BrandDto brand;
    private PromotionDto promotion;
}
