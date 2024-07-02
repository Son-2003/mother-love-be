package com.motherlove.models.payload.dto;

import com.motherlove.utils.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDto {
    private Long voucherId;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = AppConstants.VOUCHER_CODE_REGEX, message = "VoucherCode is require format: KK + 5 characters")
    private String voucherCode;

    @NotEmpty(message = "Voucher's name cannot be blank")
    @Size(min = 5, message = "Voucher's name must have at least 5 characters")
    private String voucherName;

    @DecimalMin(value = "1", message = "Quantity must be greater than 0")
    private int quantity;

    @DecimalMin(value = "1", message = "Quantity must be greater than 0")
    private int quantityUse;

    @DecimalMin(value = "1000", message = "Discount must be greater than 1.000 VND")
    private float discount;

    @DecimalMin(value = "0", message = "MinOrderAmount must be greater than 0")
    private float minOrderAmount;

    @FutureOrPresent(message = "StartDate cannot be in the past")
    private LocalDateTime startDate;

    @FutureOrPresent(message = "EndDate cannot be in the past")
    private LocalDateTime endDate;

    private int status;
}
