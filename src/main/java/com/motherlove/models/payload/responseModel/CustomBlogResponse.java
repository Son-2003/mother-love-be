package com.motherlove.models.payload.responseModel;

import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.User;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.models.payload.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomBlogResponse {
    private Long blogId;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createdDate;
    private UserDto user;
    private ProductDto product;
}
