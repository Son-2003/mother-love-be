package com.motherlove.controllers;

import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.services.VoucherService;
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
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping

    public ResponseEntity<VoucherDto> addVoucher(@RequestBody @Valid VoucherDto voucherDto) {
        VoucherDto savedVoucher = voucherService.addVoucher(voucherDto);
        return new ResponseEntity<>(savedVoucher, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("{id}")
    public ResponseEntity<VoucherDto> getVoucher(@PathVariable long id){
        return ResponseEntity.ok(voucherService.getVoucher(id));
    }

    @GetMapping
    public ResponseEntity<Object> getAllVouchers(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_VOUCHER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(voucherService.getAllVouchers(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping()
    public ResponseEntity<VoucherDto> updateVoucher(@RequestBody @Valid VoucherDto voucherDto){
        return ResponseEntity.ok(voucherService.updateVoucher(voucherDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteVoucher(@PathVariable(name = "id") long id){
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok("Voucher deleted successfully!");
    }
}
