package com.motherlove.repositories;

import com.motherlove.models.entities.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByTitle(String title);

    @Query("""
            SELECT b FROM Blog b
            where b.title like %:searchText% or b.content like %:searchText%
            """)
    Page<Blog> searchBlogs(String searchText, Pageable pageable);
}
