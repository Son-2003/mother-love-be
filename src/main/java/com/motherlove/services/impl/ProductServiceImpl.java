package com.motherlove.services.impl;

import com.motherlove.models.entities.Product;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.services.ProductService;
import com.motherlove.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
        PageRequest pageable = PageRequest.of(page - 1, size);

        Page<Product> products = productRepository.findAll(pageable);
        if(products.isEmpty()){
            throw new MotherLoveApiException(HttpStatus.NOT_FOUND, AppConstants.PRODUCT_NOT_FOUND);
        }else {
            return products.map(this::mapToDto);
        }
    }

    private ProductDto mapToDto(Product product){
        return mapper.map(product, ProductDto.class);
    }
}
