package com.motherlove.controllers;

import com.motherlove.models.payload.dto.BlogDto;
import com.motherlove.models.payload.responseModel.CustomBlogResponse;
import com.motherlove.services.BlogService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CustomBlogResponse> addBLog(@RequestBody @Valid BlogDto blogDto) {
        return ResponseEntity.ok(blogService.addBLog(blogDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("/{id}")
    public ResponseEntity<CustomBlogResponse> getBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlog(id));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok("Blog deleted successfully");
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping()
    public ResponseEntity<CustomBlogResponse> updateBlog(@RequestBody @Valid BlogDto blogDto) {
        return ResponseEntity.ok(blogService.updateBlog(blogDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping
    public ResponseEntity<Page<CustomBlogResponse>> getAllBlogs(@RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY_BLOG_ID) String sortBy,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        return ResponseEntity.ok(blogService.getAllBlogs(pageNo, pageSize, sortBy, sortDir));
    }
}
