package com.motherlove.controllers;

import com.motherlove.models.entities.Supplier;
import com.motherlove.models.payload.dto.SupplierDto;
import com.motherlove.models.payload.responseModel.SupplierResponse;
import com.motherlove.services.ISupplierService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    private final ISupplierService supplierService;

    @Autowired
    public SupplierController(ISupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierDto> addSupplier(@RequestBody @Valid SupplierDto supplierDto) {
        SupplierDto savedCategory = supplierService.addSupplier(supplierDto);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("{id}")
    public ResponseEntity<SupplierDto> getSupplier(@PathVariable long id){
        return ResponseEntity.ok(supplierService.getSupplier(id));
    }

    @GetMapping
    public ResponseEntity<SupplierResponse> getAllSuppliers(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_SUPPLIER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SupplierDto>> getAllSuppliers(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_SUPPLIER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam String searchText
    ){
        return ResponseEntity.ok(supplierService.searchSuppliers(pageNo, pageSize, sortBy, sortDir, searchText));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@RequestBody @Valid SupplierDto supplierDto, @PathVariable(name = "id") long supplierId){
        return ResponseEntity.ok(supplierService.updateSupplier(supplierId, supplierDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable long id){
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Supplier with id: " + id + " has been deleted.");
    }
}
