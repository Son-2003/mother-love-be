package com.motherlove.services;

import com.motherlove.models.payload.dto.BlogDto;
import com.motherlove.models.payload.responseModel.CustomBlogResponse;
import org.springframework.data.domain.Page;

public interface BlogService {
    CustomBlogResponse addBLog(BlogDto blogDto);
    CustomBlogResponse getBlog(Long id);
    Page<BlogDto> getAllBlogs(int pageNo, int pageSize, String sortBy, String sortDir);
    CustomBlogResponse updateBlog(BlogDto blogDto);
    void deleteBlog(long id);
}
