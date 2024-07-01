package com.motherlove.controllers;

import com.motherlove.models.payload.dto.PaymentMethodDto;
import com.motherlove.models.payload.responseModel.PaymentMethodResponse;
import com.motherlove.services.IPaymentMethodService;
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
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController {
    private final IPaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodController(IPaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentMethodDto> addPaymentMethod(@RequestBody @Valid PaymentMethodDto paymentMethodDto) {
        PaymentMethodDto savedPaymentMehotd = paymentMethodService.addPaymentMethod(paymentMethodDto);
        return new ResponseEntity<>(savedPaymentMehotd, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("{id}")
    public ResponseEntity<PaymentMethodDto> getPaymentMethod(@PathVariable long id){
        return ResponseEntity.ok(paymentMethodService.getPaymentMethod(id));
    }

    @GetMapping
    public ResponseEntity<PaymentMethodResponse> getAllPaymentMethods(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_PAYMENT_METHOD_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<PaymentMethodDto> updatePaymentMethod(@RequestBody @Valid PaymentMethodDto paymentMethodDto, @PathVariable(name = "id") long paymentMethodId){
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(paymentMethodDto, paymentMethodId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable(name = "id") long id){
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok("Payment method deleted successfully!");
    }
}
