package com.motherlove.repositories;

import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.ProductBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductBlogRepository extends JpaRepository<ProductBlog, Long> {
    @Query("SELECT p FROM ProductBlog pb join Product p on pb.product.productId = p.productId WHERE pb.blog.blogId = :blogId")
    List<Product> findProductByBlogId(Long blogId);

    ProductBlog findByBlog_BlogIdAndProduct_ProductId(Long blogId, Long ProductId);
    List<ProductBlog> findByBlog_BlogId(Long blogId);
}
