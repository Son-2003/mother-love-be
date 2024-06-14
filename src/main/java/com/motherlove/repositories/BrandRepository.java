package com.motherlove.repositories;

import com.motherlove.models.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> searchBrandByBrandNameContaining(String keyword);
}
