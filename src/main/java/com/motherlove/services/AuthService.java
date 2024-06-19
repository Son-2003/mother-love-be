package com.motherlove.services;

import com.motherlove.models.payload.dto.SignupDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    JWTAuthResponse authenticateUser(LoginDto loginDto);
    JWTAuthResponse signupMember(SignupDto signupDto);
    JWTAuthResponse signupStaff(SignupDto signupDto);
    UserDto getCustomerInfo();
    ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response);
}
