package com.motherlove.repositories;

import com.motherlove.models.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUser_UserIdAndAddressId(Long userId, Long addressId);
    Page<Address> findByUser_UserId(Long userId, Pageable pageable);
}
