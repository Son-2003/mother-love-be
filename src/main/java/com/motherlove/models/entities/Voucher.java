package com.motherlove.models.entities;

import com.motherlove.models.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;

    @Column(nullable = false, unique = true)
    private String voucherCode;

    @Column(nullable = false)
    private String voucherName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int quantityUse;

    @Column(nullable = false)
    private float discount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private float minOrderAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        ZonedDateTime nowInVietnam = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        this.createdDate = nowInVietnam.toLocalDateTime();
        this.lastModifiedDate = nowInVietnam.toLocalDateTime();
        this.startDate = nowInVietnam.toLocalDateTime();
        this.endDate = nowInVietnam.toLocalDateTime();
    }

    @PreUpdate
    protected void onUpdate() {
        ZonedDateTime nowInVietnam = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        this.lastModifiedDate = nowInVietnam.toLocalDateTime();
        this.startDate = nowInVietnam.toLocalDateTime();
        this.endDate = nowInVietnam.toLocalDateTime();
    }

    @OneToMany(mappedBy = "voucher")
    private Set<CustomerVoucher> customerVouchers;

    @OneToMany(mappedBy = "voucher")
    private Set<Order> orders;
}
