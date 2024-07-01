package com.motherlove.services;

import com.motherlove.models.payload.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    Page<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto deleteProduct(long id);

    ProductDto getProductById(long id);

    ProductDto addProduct(ProductDto productDto);

    List<ProductDto> getProductsByBrandAndCategory(Long brandId, Long categoryId);
}
