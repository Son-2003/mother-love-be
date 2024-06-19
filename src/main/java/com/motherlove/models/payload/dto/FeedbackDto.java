package com.motherlove.models.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private Long feedbackId;
    private int rating;
    private String comment;
    private String image;
    private LocalDateTime feedbackDate;
    private Long productId;
    private Long userId;
}
