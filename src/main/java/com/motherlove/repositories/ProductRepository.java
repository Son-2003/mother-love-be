package com.motherlove.repositories;

import com.motherlove.models.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("SELECT p FROM Product p WHERE (p.brand.brandId = :brandId OR :brandId is null) AND (p.category.categoryId = :categoryId or :categoryId is null)")
    List<Product> getProductsByBrandIdAndCategoryId(Long brandId, Long categoryId);
}
