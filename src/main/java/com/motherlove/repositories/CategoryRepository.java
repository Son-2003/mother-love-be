package com.motherlove.repositories;

import com.motherlove.models.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE %:keyword%")
    Page<Category> searchCategories(String keyword, Pageable pageable);
}
