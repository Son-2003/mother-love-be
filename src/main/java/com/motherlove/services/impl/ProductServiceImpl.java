package com.motherlove.services.impl;

import com.motherlove.models.entities.Product;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private ProductDto mapToDto(Product product) {
        return mapper.map(product, ProductDto.class);
    }

    @Override
    public Page<ProductDto> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToDto);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        if (!productRepository.existsById(productDto.getProductId())) {
            throw new ResourceNotFoundException(Product.class.getName(), "ID", productDto.getProductId());
        }
        Product product = mapper.map(productDto, Product.class);
        return mapToDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto deleteProduct(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Product.class.getName(), "ID", id));
        productRepository.delete(product);
        return mapToDto(product);
    }

    @Override
    public ProductDto getProductById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Product.class.getName(), "ID", id));
        return mapToDto(product);
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Product savedProduct = productRepository.save(mapper.map(productDto, Product.class));
        return mapToDto(savedProduct);
    }

    @Override
    public List<ProductDto> getProductsByBrandAndCategory(Long brandId, Long categoryId) {
        return productRepository.getProductsByBrandIdAndCategoryId(brandId, categoryId)
                .stream()
                .map(this::mapToDto).toList();
    }
}
