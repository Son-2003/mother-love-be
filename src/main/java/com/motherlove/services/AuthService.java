package com.motherlove.services;

import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;

public interface AuthService {
    JWTAuthResponse authenticateUser(LoginDto loginDto);
    UserDto getCustomerInfo();
}
