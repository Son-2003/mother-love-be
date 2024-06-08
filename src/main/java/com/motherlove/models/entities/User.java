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

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<CustomerVoucher> customerVouchers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Feedback> feedbacks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PointTransaction> pointTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Report> reports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Blog> blogs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Token> tokens;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
}
