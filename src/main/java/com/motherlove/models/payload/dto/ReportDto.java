package com.motherlove.models.payload.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long reportId;

    @NotNull(message = "Report type cannot be null")
    private int reportType;

    @NotEmpty(message = "Report's content cannot be blank")
    @Size(min = 3, message = "Report's content must have at least 3 characters")
    private String content;

    private String response;

    private String image;

    private CustomUserDto questioner;

    private CustomUserDto answerer;
}
