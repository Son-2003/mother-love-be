package com.motherlove.models.entities;

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
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    @Column(nullable = false)
    private String promotionName;

    @Column(nullable = false, length = 65535)
    private String description;

    @Column(nullable = false)
    private int quantityOfGift;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean status;

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

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "giftId", referencedColumnName = "productId")
    private Product gift;

    @OneToMany(mappedBy = "promotion")
    private Set<OrderDetail> orderDetails;
}
