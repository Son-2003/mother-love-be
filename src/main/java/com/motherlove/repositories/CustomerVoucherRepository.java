package com.motherlove.repositories;

import com.motherlove.models.entities.CustomerVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    @Query("SELECT cv FROM CustomerVoucher cv " +
            "INNER JOIN cv.voucher v " +
            "INNER JOIN cv.user u " +
            "WHERE :now < v.endDate AND cv.isUsed = false AND u.userId = :userId " +
            "ORDER BY v.voucherId ASC")
    Page<CustomerVoucher> findVouchersOfMember(LocalDateTime now, Long userId, Pageable pageable);
    CustomerVoucher findByVoucher_VoucherIdAndUser_UserId(Long voucherId, Long userId);
}
