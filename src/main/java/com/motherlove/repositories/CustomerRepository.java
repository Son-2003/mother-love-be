package com.motherlove.repositories;

import com.motherlove.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerAccountOrEmailOrPhone(String email, String customerAccount, String phone);
}
