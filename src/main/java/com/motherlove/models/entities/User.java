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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private boolean isFirstLogin;

    @Column(nullable = false, length = 65535)
    private String image;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @OneToMany(mappedBy = "user")
    private Set<CustomerVoucher> customerVouchers;

    @OneToMany(mappedBy = "user")
    private Set<Feedback> feedbacks;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses;

    @OneToMany(mappedBy = "user")
    private Set<PointTransaction> pointTransactions;

    @OneToMany(mappedBy = "questioner")
    private Set<Report> reports;

    @OneToMany(mappedBy = "answerer")
    private Set<Report> reportResponses;

    @OneToMany(mappedBy = "user")
    private Set<Blog> blogs;

    @OneToMany(mappedBy = "user")
    private Set<Token> tokens;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
}
