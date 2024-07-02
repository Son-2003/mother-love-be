package com.motherlove.services.impl;

import com.motherlove.models.entities.Brand;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.BrandDto;
import com.motherlove.models.payload.responseModel.BrandResponse;
import com.motherlove.repositories.BrandRepository;
import com.motherlove.services.IBrandService;
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
public class BrandServiceImpl implements IBrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper mapper;
    
    public BrandDto mapEntityToDto(Brand entity){
        return mapper.map(entity, BrandDto.class);
    }

    public BrandResponse mapEntityToBrandResponse(Brand entity){
        return mapper.map(entity, BrandResponse.class);
    }

    @Override
    @Transactional
    public Page<BrandResponse> getAllBrands(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Brand> brands = brandRepository.findAll(pageable);
        return brands.map(this::mapEntityToBrandResponse);
    }

    @Override
    @Transactional
    public BrandDto getBrandById(long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Brand.class.getName(), "ID", id));
        return mapEntityToDto(brand);
    }

    @Override
    public BrandDto addBrand(BrandDto brandDto) {
        Brand savedBrand = brandRepository.save(mapper.map(brandDto, Brand.class));
        return mapEntityToDto(savedBrand);
    }

    @Override
    public BrandDto updateBrand(BrandDto brandDto) {
        if (!brandRepository.existsById(brandDto.getBrandId())) {
            throw new ResourceNotFoundException(Brand.class.getName(), "ID", brandDto.getBrandId());
        }
        Brand brand = mapper.map(brandDto, Brand.class);
        return mapEntityToDto(brandRepository.save(brand));
    }

    @Override
    public BrandDto deleteBrand(long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Brand.class.getName(), "ID", id));
        brandRepository.delete(brand);
        return mapEntityToDto(brand);
    }

    @Override
    public List<BrandDto> searchBrands(String keyword) {
        return brandRepository
                .searchBrandByBrandNameContaining(keyword)
                .stream()
                .map(this::mapEntityToDto)
                .toList();
    }
}
