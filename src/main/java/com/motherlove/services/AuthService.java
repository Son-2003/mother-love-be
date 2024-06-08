package com.motherlove.services;

import com.motherlove.models.payload.dto.SignupDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;

public interface AuthService {
    JWTAuthResponse authenticateUser(LoginDto loginDto);
    JWTAuthResponse signupMember(SignupDto signupDto);
    UserDto getCustomerInfo();
}
