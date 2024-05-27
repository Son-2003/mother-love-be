package com.motherlove.services;

import com.motherlove.models.payload.dto.ProductDto;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductDto> getAllProducts(int page, int size);
}
