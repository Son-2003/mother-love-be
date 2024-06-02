package com.motherlove.models.payload.dto;

import com.motherlove.models.entities.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long categoryId;

    @NotEmpty(message = "Username or email cannot be blank")
    @Size(min = 8, message = "Username, email or phone must have at least 8 characters")
    private String categoryName;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
