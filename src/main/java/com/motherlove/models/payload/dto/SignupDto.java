package com.motherlove.models.payload.dto;

import com.motherlove.utils.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {
    @NotBlank(message = "Username, email cannot be blank")
    @Size(min = 8, message = "Username, email must have at least 8 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    @Pattern(regexp = AppConstants.EMAIL_REGEX, message = "Email is invalid!")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = "Password must be 8-12 characters with at least one uppercase letter, one number, and one special character (!@#$%^&*).")
    private String password;

    @NotBlank(message = "FullName cannot be blank")
    private String fullName;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = AppConstants.PHONE_REGEX, message = "Invalid phone number!")
    private String phone;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = AppConstants.GENDER_REGEX, message = "Gender include: Male, Female, Order")
    private String gender;
}
