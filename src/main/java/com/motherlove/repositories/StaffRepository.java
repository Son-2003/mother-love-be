package com.motherlove.repositories;

import com.motherlove.models.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByStaffAccountOrEmailOrPhone(String email, String staffAccount, String phone);
}
