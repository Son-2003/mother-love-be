package com.motherlove.repositories;

import com.motherlove.models.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByTitle(String title);
}
