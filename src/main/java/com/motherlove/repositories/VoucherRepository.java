package com.motherlove.repositories;

import com.motherlove.models.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Voucher findByVoucherCode(String voucherCode);
    @Query("SELECT v FROM Voucher v WHERE (:now < v.endDate) and (v.quantity > 0)")
    Page<Voucher> findVouchersValidAt(LocalDateTime now, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE ((:now > v.endDate or v.quantity = 0) and v.status = 1)")
    List<Voucher> findVoucherExpire(LocalDateTime now);
}
