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
public class FeedbackUpdateReq {
    @Min(value = 0, message = "FeedbackId must be at least 0")
    private Long feedbackId;

    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at least 0")
    private int rating;

    private String comment;

    private String image;
}
