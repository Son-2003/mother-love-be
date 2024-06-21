package com.motherlove.models.payload.requestModel;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCreateReq {
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at least 0")
    private int rating;

    private String comment;

    private String image;

    @Min(value = 0, message = "ProductId must be at least 0")
    private Long productId;
}
