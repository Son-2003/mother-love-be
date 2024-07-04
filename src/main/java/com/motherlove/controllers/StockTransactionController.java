package com.motherlove.controllers;

import com.motherlove.models.payload.dto.StockTransactionDto;
import com.motherlove.models.payload.requestModel.ImportProduct;
import com.motherlove.services.IStockTransactionService;
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
@RequestMapping("/api/v1/stock_transactions")
@RequiredArgsConstructor
public class StockTransactionController {
    private final IStockTransactionService stockTransactionService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> importProduct(@Valid @RequestBody ImportProduct importProduct) {
        StockTransactionDto stockTransactionDto = stockTransactionService.importProduct(importProduct);
        return new ResponseEntity<>(stockTransactionDto, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<Object> getAllStockTransaction(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_STOCK_TRANSACTION_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(stockTransactionService.getAllStockTransaction(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{stockTransactionId}")
    public ResponseEntity<Object> getStockTransaction(@PathVariable(name = "stockTransactionId") Long stockTransactionId){
        return ResponseEntity.ok(stockTransactionService.getStockTransactionById(stockTransactionId));
    }
}
