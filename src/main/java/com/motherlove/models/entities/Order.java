package com.motherlove.models.entities;

import com.motherlove.models.enums.OrderStatus;
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
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private boolean isFeedBack;

    @Column(nullable = false)
    private float totalAmount;

    @Column(nullable = false)
    private float afterTotalAmount;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @OneToOne(mappedBy = "order")
    private OrderCancel orderCancel;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "addressId", referencedColumnName = "addressId", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private PaymentHistory paymentHistory;

    @OneToMany(mappedBy = "order")
    private Set<Feedback> feedback;

    @ManyToOne
    @JoinColumn(name = "voucherId", referencedColumnName = "voucherId", nullable = true)
    private Voucher voucher;
}
