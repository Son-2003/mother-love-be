package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.models.payload.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CustomBlogResponse {
    private Long blogId;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private UserDto user;
    private List<ProductDto> product;
}
