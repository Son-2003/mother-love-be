package com.motherlove.services;

import com.motherlove.models.payload.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface IProductService {
    Page<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<ProductDto> searchProduct(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams);
    ProductDto updateProduct(ProductDto productDto);
    ProductDto deleteProduct(long id);
    ProductDto getProductById(long id);
    ProductDto addProduct(ProductDto productDto);
}
