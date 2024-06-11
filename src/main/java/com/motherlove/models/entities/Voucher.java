package com.motherlove.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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
    private float discount;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private float minOrderAmount;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "voucher")
    private Set<CustomerVoucher> customerVouchers;

    @OneToMany(mappedBy = "voucher")
    private Set<OrderVoucher> orderVouchers;
}
