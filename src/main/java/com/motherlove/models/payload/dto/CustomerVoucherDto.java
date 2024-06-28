package com.motherlove.models.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucherDto {
    private Long customerVoucherId;
    private boolean isUsed;
    private int quantity;
    private LocalDateTime assignedDate;
    private LocalDateTime usedDate;
    private VoucherDto voucher;
    private UserDto user;
}
