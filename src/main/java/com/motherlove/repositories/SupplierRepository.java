package com.motherlove.repositories;

import com.motherlove.models.entities.Blog;
import com.motherlove.models.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{
    Supplier findBySupplierName(String supplierName);

    @Query("""
            SELECT s FROM Supplier s
            where s.address like %:searchText% or s.contactInfo like %:searchText%
            or s.email like %:searchText% or s.supplierName like %:searchText%
            """)
    Page<Supplier> searchSuppliers(String searchText, Pageable pageable);
}
