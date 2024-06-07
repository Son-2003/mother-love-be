package com.motherlove.controllers;

import com.motherlove.models.payload.dto.BrandDto;
import com.motherlove.services.IBrandService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brand")
@RequiredArgsConstructor
public class BrandController {

    private final IBrandService brandService;

    @Operation(summary = "Get List Brands", description = "Get List Brands")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping
    public ResponseEntity<Object> getAllBrands(@RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo, @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize, @RequestParam(name = "sortBy", defaultValue = "brandId", required = false) String sortBy, @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return ResponseEntity.ok(brandService.getAllBrands(pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("{id}")
    public ResponseEntity<BrandDto> getCategory(@PathVariable long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<BrandDto> addCategory(@RequestBody BrandDto brandDto) {
        BrandDto savedBrand = brandService.addBrand(brandDto);
        return ResponseEntity.ok(savedBrand);
    }

    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<BrandDto> updateCategory(@RequestBody BrandDto productDto) {
        return ResponseEntity.ok(brandService.updateBrand(productDto));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BrandDto> deleteCategory(@PathVariable(name = "id") long id) {
        BrandDto deletedBrandDto = brandService.deleteBrand(id);
        return ResponseEntity.ok(deletedBrandDto);
    }

}
