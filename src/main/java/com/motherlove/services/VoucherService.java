package com.motherlove.services;

import com.motherlove.models.entities.Order;
import com.motherlove.models.payload.dto.CustomerVoucherDto;
import com.motherlove.models.payload.dto.VoucherDto;
import org.springframework.data.domain.Page;

public interface VoucherService {
    VoucherDto addVoucher(VoucherDto voucherDto);
    VoucherDto getVoucher(Long id);
    Page<VoucherDto> getAllVouchersInManage(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<VoucherDto> getAllVouchers(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<CustomerVoucherDto> getAllVouchersOfMember(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    VoucherDto updateVoucher(VoucherDto voucherDto);
    void deleteVoucher(long id);
    CustomerVoucherDto addVoucherForUser(Long userId, Long voucherId);
    void handleVoucherInOrder(Long voucherId, Long userId, Order order);
}
