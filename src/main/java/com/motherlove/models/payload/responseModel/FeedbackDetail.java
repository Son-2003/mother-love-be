package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDetail {
    private Long feedbackId;
    private int rating;
    private String comment;
    private String image;
    private LocalDateTime feedbackDate;
    private UserDto user;
}
