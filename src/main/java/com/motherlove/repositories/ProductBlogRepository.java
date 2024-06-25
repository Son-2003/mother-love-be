package com.motherlove.repositories;

import com.motherlove.models.entities.ProductBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductBlogRepository extends JpaRepository<ProductBlog, Long> {
    @Query("SELECT pb FROM ProductBlog pb WHERE pb.blog.blogId = :blogId")
    ProductBlog findByBlogId(Long blogId);
}
