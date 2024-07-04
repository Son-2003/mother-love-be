package com.motherlove.models.payload.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlogDto {
    private Long blogId;

    @NotEmpty(message = "Blog's title cannot be blank")
    @Size(min = 2, message = "Blog's title must have at least 2 characters")
    private String title;

    @NotEmpty(message = "Blog's content cannot be blank")
    @Size(min = 2, message = "Blog's content must have at least 2 characters")
    private String content;

    @NotEmpty(message = "Blog's image cannot be blank")
    private String image;

    @NotNull(message = "Blog's author cannot be null")
    private Long userId;

    @NotNull(message = "Blog's product cannot be null")
    private List<Long> productId;
}
