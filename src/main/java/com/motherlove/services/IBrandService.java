package com.motherlove.services;

import com.motherlove.models.payload.dto.BrandDto;
import com.motherlove.models.payload.responseModel.BrandResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBrandService {
    Page<BrandResponse> getAllBrands(int pageNo, int pageSize, String sortBy, String sortDir);

    BrandDto getBrandById(long id);

    BrandDto addBrand(BrandDto brandDto);

    BrandDto updateBrand(BrandDto productDto);

    BrandDto deleteBrand(long id);

    List<BrandDto> searchBrands(String keyword);
}
