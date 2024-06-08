package com.motherlove.repositories;

import com.motherlove.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserNameOrEmailOrPhone(String email, String userName, String phone);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}
