package com.motherlove.controllers;

import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.dto.PaymentHistoryDto;
import com.motherlove.models.payload.responseModel.CategoryResponse;
import com.motherlove.models.payload.responseModel.CustomPaymentHistoryResponse;
import com.motherlove.models.payload.responseModel.PaymentHistoryResponse;
import com.motherlove.services.PaymentHistoryService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment-histories")
public class PaymentHistoryController {
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    public PaymentHistoryController(PaymentHistoryService paymentHistoryService) {
        this.paymentHistoryService = paymentHistoryService;
    }

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PostMapping
    public ResponseEntity<CustomPaymentHistoryResponse> addPaymentHistory(@RequestBody @Valid PaymentHistoryDto paymentHistoryDto) {
        CustomPaymentHistoryResponse paymentHistory = paymentHistoryService.addPaymentHistory(paymentHistoryDto);
        return new ResponseEntity<>(paymentHistory, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("{id}")
    public ResponseEntity<CustomPaymentHistoryResponse> getPaymentHistory(@PathVariable long id){
        return ResponseEntity.ok(paymentHistoryService.getPaymentHistory(id));
    }

    @GetMapping
    public ResponseEntity<PaymentHistoryResponse> getAllPaymentHistories(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_PAYMENT_HISTORY_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(paymentHistoryService.getAllPaymentHistories(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PutMapping("{id}")
    public ResponseEntity<CustomPaymentHistoryResponse> updatePaymentHistory(@RequestBody @Valid PaymentHistoryDto paymentHistoryDto, @PathVariable(name = "id") long paymentHistoryId){
        return ResponseEntity.ok(paymentHistoryService.updatePaymentHistory(paymentHistoryDto, paymentHistoryId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePaymentHistory(@PathVariable(name = "id") long id){
        paymentHistoryService.deletePaymentHistory(id);
        return ResponseEntity.ok("Payment history deleted successfully!");
    }
}
