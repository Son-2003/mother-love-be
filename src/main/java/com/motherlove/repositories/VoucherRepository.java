package com.motherlove.repositories;

import com.motherlove.models.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Voucher findByVoucherCode(String voucherCode);
}
