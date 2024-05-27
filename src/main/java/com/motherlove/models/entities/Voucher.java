package com.motherlove.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
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

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private Set<CustomerVoucher> customerVouchers;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private Set<OrderVoucher> orderVouchers;
}
