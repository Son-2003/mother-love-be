package com.motherlove.services.impl;

import com.motherlove.models.entities.Product;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.services.IProductService;
import com.motherlove.utils.GenericSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public Page<ProductDto> searchProduct(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Product> specification = specification(searchParams);

        Page<Product> productsPage = productRepository.findAll(specification, pageable);

        return productsPage.map(this::mapToDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (!productRepository.existsById(productDto.getProductId())) {
            throw new ResourceNotFoundException(Product.class.getName(), "ID", productDto.getProductId());
        }
        Product product = mapper.map(productDto, Product.class);
        return mapToDto(productRepository.save(product));
    }

    @Override
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
    public ProductDto addProduct(ProductDto productDto) {
        Product savedProduct = productRepository.save(mapper.map(productDto, Product.class));
        return mapToDto(savedProduct);
    }

    private Specification<Product> specification(Map<String, Object> searchParams){
        List<Specification<Product>> specs = new ArrayList<>();

        // Lặp qua từng entry trong searchParams để tạo các Specification tương ứng
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "status" -> specs.add(GenericSpecification.fieldIn(key, (Collection<?>) value));
                case "productName" -> specs.add(GenericSpecification.fieldContains(key, (String) value));
                case "brandName" -> specs.add(GenericSpecification.joinFieldIn("brand", key, (Collection<?>) value));
                case "categoryName" ->
                        specs.add(GenericSpecification.joinFieldIn("category", key, (Collection<?>) value));
            }
        });

        // Tổng hợp tất cả các Specification thành một Specification duy nhất bằng cách sử dụng phương thức reduce của Stream
        //reduce de ket hop cac spec1(dk1), spec2(dk2),.. thanh 1 specification chung va cac spec ket hop voi nhau thono qua AND
        return specs.stream().reduce(Specification.where(null), Specification::and);
    }
}
