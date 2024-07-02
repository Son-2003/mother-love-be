package com.motherlove.repositories;

import com.motherlove.models.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserNameOrEmailOrPhone(String email, String userName, String phone);
    @Query("SELECT u FROM User u WHERE u.role.roleName <> 'ROLE_ADMIN'")
    List<User> findAllAccountByAdmin(Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.role.roleName = 'ROLE_MEMBER'")
    List<User> findAllAccountMember(Pageable pageable);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);

}
