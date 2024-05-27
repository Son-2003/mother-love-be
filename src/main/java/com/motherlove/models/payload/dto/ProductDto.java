package com.motherlove.models.payload.dto;

import com.motherlove.models.entities.Brand;
import com.motherlove.models.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String productName;
    private String description;
    private float price;
    private int status;
    private String image;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Category category;
    private Brand brand;
}
