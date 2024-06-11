package com.motherlove.repositories;

import com.motherlove.models.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{
    Supplier findBySupplierName(String supplierName);
}
