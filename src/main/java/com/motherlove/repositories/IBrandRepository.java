package com.motherlove.repositories;

import com.motherlove.models.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBrandRepository extends JpaRepository<Brand, Long> {
}
