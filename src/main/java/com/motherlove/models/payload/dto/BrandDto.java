package com.motherlove.models.payload.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {
    private Long brandId;

    @NotEmpty(message = "Username or email cannot be blank")
    @Size(min = 8, message = "Username, email or phone must have at least 8 characters")
    private String brandName;
}
