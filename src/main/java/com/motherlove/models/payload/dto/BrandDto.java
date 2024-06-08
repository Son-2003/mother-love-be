package com.motherlove.models.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {

    private Long brandId;
    private String brandName;
    private String image;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<ProductDto> products;
}