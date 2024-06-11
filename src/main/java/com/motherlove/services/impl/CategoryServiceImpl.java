package com.motherlove.services.impl;

import com.motherlove.models.entities.Category;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.responseModel.CategoryResponse;
import com.motherlove.repositories.CategoryRepository;
import com.motherlove.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryResponse getAllCategories(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Category> categories = categoryRepository.findAll(pageable);

        //get content of page
        List<Category> categoryList = categories.getContent();

        //format the response
        List<CategoryDto> content = categoryList.stream().map(category -> modelMapper.map(category, CategoryDto.class)).collect(Collectors.toList());
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(content);
        categoryResponse.setPageNo(categories.getNumber());
        categoryResponse.setPageSize(categories.getSize());
        categoryResponse.setTotalElements(categories.getTotalElements());
        categoryResponse.setTotalPages(categories.getTotalPages());
        categoryResponse.setLast(categories.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDto.getCategoryId()));
        category.setCategoryName(categoryDto.getCategoryName());
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void deleteCategory(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (!category.getProducts().isEmpty()){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "There is at least one product belongs to this category");
        }
        categoryRepository.delete(category);
    }
}
