package com.motherlove.services;

import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.responseModel.CategoryResponse;
import org.springframework.data.domain.Page;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    CategoryDto getCategory(Long id);
    CategoryResponse getAllCategories(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<CategoryDto> searchCategories(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
    CategoryDto updateCategory(CategoryDto categoryDto);
    void deleteCategory(long id);
}
