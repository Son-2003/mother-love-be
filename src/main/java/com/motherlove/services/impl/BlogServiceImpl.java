package com.motherlove.services.impl;

import com.motherlove.models.entities.Blog;
import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.ProductBlog;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.BlogDto;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.responseModel.CustomBlogResponse;
import com.motherlove.repositories.BlogRepository;
import com.motherlove.repositories.ProductBlogRepository;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.services.IBlogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImpl implements IBlogService {
    private final BlogRepository blogRepository;
    private final ProductBlogRepository productBlogRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public CustomBlogResponse addBLog(BlogDto blogDto) {
        List<ProductBlog> productBlogList = new ArrayList<>();
        Blog blog = mapper.map(blogDto, Blog.class);
        Blog checkDuplicate = blogRepository.findByTitle(blog.getTitle());
        if (checkDuplicate != null) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Blog with title " + blog.getTitle() + " already exists");
        }
        Blog savedBlog = blogRepository.save(blog);

        blogDto.getProductId().forEach(tmp -> {
            Product product = productRepository.findById(tmp).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Product with id " + tmp + " not found"));
            ProductBlog productBlog = ProductBlog.builder().blog(savedBlog).product(product).build();
            productBlogList.add(productBlog);
        });
        productBlogRepository.saveAll(productBlogList);
        return customResponse(savedBlog);
    }

    @Override
    public CustomBlogResponse getBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + id + " not found"));
        return customResponse(blog);
    }

    @Override
    public Page<CustomBlogResponse> getAllBlogs(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Blog> blogs = blogRepository.findAll(pageable);

        return blogs.map(this::customResponse);
    }

    @Override
    public Page<CustomBlogResponse> searchBlogs(int pageNo, int pageSize, String sortBy, String sortDir, String searchText) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Blog> blogs = blogRepository.searchBlogs(searchText, pageable);

        return blogs.map(this::customResponse);
    }

    public CustomBlogResponse customResponse(Blog blog){
        CustomBlogResponse blogCustomResponse = new CustomBlogResponse();
        blogCustomResponse.setBlogId(blog.getBlogId());
        blogCustomResponse.setTitle(blog.getTitle());
        blogCustomResponse.setContent(blog.getContent());
        blogCustomResponse.setImage(blog.getImage());
        blogCustomResponse.setCreatedDate(blog.getCreatedDate());
        blogCustomResponse.setLastModifiedDate(blog.getLastModifiedDate());
        UserDto userDto = mapper.map(blog.getUser(), UserDto.class);
        List<Product> products = productBlogRepository.findProductByBlogId(blog.getBlogId());
        blogCustomResponse.setUser(userDto);
        blogCustomResponse.setProduct(products.stream().map(product -> mapper.map(product, ProductDto.class)).toList());
        return blogCustomResponse;
    }

    @Override
    public CustomBlogResponse updateBlog(BlogDto blogDto) {
        List<ProductBlog> productBlogNew = new ArrayList<>();
        List<ProductBlog> productBlogCurrent = new ArrayList<>();

        Blog blog = blogRepository.findById(blogDto.getBlogId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + blogDto.getBlogId() + " not found"));
        if (!blog.getTitle().equalsIgnoreCase(blogDto.getTitle())) {
            Blog checkDuplicateTitle = blogRepository.findByTitle(blogDto.getTitle());
            if (checkDuplicateTitle != null) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Blog with title " + blogDto.getTitle() + " already exists");
            }
        }

        // List current Products
        List<Product> currentProducts = productBlogRepository.findProductByBlogId(blogDto.getBlogId());
        currentProducts.forEach(tmp -> productBlogCurrent.add(productBlogRepository.findByBlog_BlogIdAndProduct_ProductId(blog.getBlogId(), tmp.getProductId())));
        productBlogRepository.deleteAll(productBlogCurrent);

        // List new Products
        List<Product> newProducts = blogDto.getProductId().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Product with id " + productId + " not found")))
                .toList();
        newProducts.forEach(tmp -> {
            ProductBlog productBlog = ProductBlog.builder().blog(blog).product(tmp).build();
            productBlogNew.add(productBlog);
        });
        productBlogRepository.saveAll(productBlogNew);

        return customResponse(blog);
    }

    @Override
    public void deleteBlog(long id) {
        List<ProductBlog> productBlog = productBlogRepository.findByBlog_BlogId(id);
        productBlogRepository.deleteAll(productBlog);
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + id + " not found"));
        blogRepository.delete(blog);
    }
}
