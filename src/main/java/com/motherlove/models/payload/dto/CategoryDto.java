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

    @NotEmpty(message = "Category's name cannot be blank")
    @Size(min = 2, message = "Category's name must have at least 2 characters")
    private String categoryName;

}
