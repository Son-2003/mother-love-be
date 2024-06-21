package com.motherlove.controllers;

import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.responseModel.CategoryResponse;
import com.motherlove.services.CategoryService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping

    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        CategoryDto savedCategory = categoryService.addCategory(categoryDto);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable long id){
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_CATEGORY_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(categoryService.getAllCategories(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping()
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto){
        return ResponseEntity.ok(categoryService.updateCategory(categoryDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "id") long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully!");
    }
}
