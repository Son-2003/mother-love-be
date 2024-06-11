package com.motherlove.services;

import com.motherlove.models.payload.dto.VoucherDto;
import org.springframework.data.domain.Page;

public interface VoucherService {
    VoucherDto addVoucher(VoucherDto voucherDto);
    VoucherDto getVoucher(Long id);
    Page<VoucherDto> getAllVouchers(int pageNo, int pageSize, String sortBy, String sortDir);
    VoucherDto updateVoucher(VoucherDto voucherDto);
    void deleteVoucher(long id);
}
