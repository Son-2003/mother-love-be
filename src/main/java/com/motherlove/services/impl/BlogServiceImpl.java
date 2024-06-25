package com.motherlove.services.impl;

import com.motherlove.models.entities.Blog;
import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.ProductBlog;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.BlogDto;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.responseModel.CustomBlogResponse;
import com.motherlove.repositories.BlogRepository;
import com.motherlove.repositories.ProductBlogRepository;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.BlogService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {
    private BlogRepository blogRepository;
    private ProductBlogRepository productBlogRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private ModelMapper mapper;

    public BlogServiceImpl(BlogRepository blogRepository, ProductBlogRepository productBlogRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper mapper) {
        this.blogRepository = blogRepository;
        this.productBlogRepository = productBlogRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public CustomBlogResponse addBLog(BlogDto blogDto) {
        Blog blog = mapper.map(blogDto, Blog.class);
        Blog checkDuplicate = blogRepository.findByTitle(blog.getTitle());
        if (checkDuplicate != null) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Blog with title " + blog.getTitle() + " already exists");
        }
        User user = userRepository.findById(blogDto.getUserId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "User with id " + blogDto.getUserId() + " not found"));
        Product product = productRepository.findById(blogDto.getProductId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Product with id " + blogDto.getProductId() + " not found"));
        Blog savedBlog = blogRepository.save(blog);
        ProductBlog productBlog = ProductBlog.builder().blog(savedBlog).product(product).build();
        productBlogRepository.save(productBlog);
        CustomBlogResponse blogCustomResponse = mapper.map(savedBlog, CustomBlogResponse.class);
        UserDto userDto = mapper.map(user, UserDto.class);
        ProductDto productDto = mapper.map(product, ProductDto.class);
        blogCustomResponse.setUser(userDto);
        blogCustomResponse.setProduct(productDto);
        return blogCustomResponse;
    }

    @Override
    public CustomBlogResponse getBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + id + " not found"));
        UserDto userDto = mapper.map(blog.getUser(), UserDto.class);
        ProductBlog productBlog = productBlogRepository.findByBlogId(id);
        ProductDto productDto = mapper.map(productBlog.getProduct(), ProductDto.class);
        CustomBlogResponse blogCustomResponse = mapper.map(blog, CustomBlogResponse.class);
        blogCustomResponse.setUser(userDto);
        blogCustomResponse.setProduct(productDto);
        return blogCustomResponse;
    }

    @Override
    public Page<CustomBlogResponse> getAllBlogs(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Blog> blogs = blogRepository.findAll(pageable);

        return blogs.map(blog -> {
            CustomBlogResponse blogCustomResponse = mapper.map(blog, CustomBlogResponse.class);
            UserDto userDto = mapper.map(blog.getUser(), UserDto.class);
            ProductBlog productBlog = productBlogRepository.findByBlogId(blog.getBlogId());
            ProductDto productDto = mapper.map(productBlog.getProduct(), ProductDto.class);
            blogCustomResponse.setUser(userDto);
            blogCustomResponse.setProduct(productDto);
            return blogCustomResponse;
        });
    }

    @Override
    public CustomBlogResponse updateBlog(BlogDto blogDto) {
        Blog blog = blogRepository.findById(blogDto.getBlogId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + blogDto.getBlogId() + " not found"));
        if (!blog.getTitle().equalsIgnoreCase(blogDto.getTitle())) {
            Blog checkDuplicateTitle = blogRepository.findByTitle(blogDto.getTitle());
            if (checkDuplicateTitle != null) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Blog with title " + blogDto.getTitle() + " already exists");
            }
        }

        ProductBlog productBlog = productBlogRepository.findByBlogId(blogDto.getBlogId());
        Product product = productRepository.findById(blogDto.getProductId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Product with id " + blogDto.getProductId() + " not found"));
        if (productBlog.getProduct().getProductId() != blogDto.getProductId()) {
            productBlog.setProduct(product);
            productBlogRepository.save(productBlog);
        }

        User user = userRepository.findById(blogDto.getUserId()).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "User with id " + blogDto.getUserId() + " not found"));
        Blog updatedBlog = blogRepository.save(mapper.map(blogDto, Blog.class));

        CustomBlogResponse blogCustomResponse = mapper.map(updatedBlog, CustomBlogResponse.class);
        blogCustomResponse.setProduct(mapper.map(product, ProductDto.class));
        blogCustomResponse.setUser(mapper.map(user, UserDto.class));
        return blogCustomResponse;
    }

    @Override
    public void deleteBlog(long id) {
        ProductBlog productBlog = productBlogRepository.findByBlogId(id);
        productBlogRepository.delete(productBlog);
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Blog with id " + id + " not found"));
        blogRepository.delete(blog);
    }
}
