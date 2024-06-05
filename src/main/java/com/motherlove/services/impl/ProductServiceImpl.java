package com.motherlove.services.impl;

import com.motherlove.models.entities.Product;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public Page<ProductDto> getAllProducts(int page, int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page - 1, size));
        return products.map(this::mapToDto);
    }

    private ProductDto mapToDto(Product product) {
        return mapper.map(product, ProductDto.class);
    }
}
