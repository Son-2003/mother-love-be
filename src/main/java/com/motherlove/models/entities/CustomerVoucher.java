package com.motherlove.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customer_voucher")
public class CustomerVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerVoucherId;

    @Column(nullable = false)
    private boolean isUsed;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime assignedDate;

    @Column(nullable = false)
    private LocalDateTime usedDate;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "voucherId", nullable = false)
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}
