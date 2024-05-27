package com.motherlove.services;

import com.motherlove.models.payload.dto.CustomerDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.dto.StaffDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;

public interface AuthService {
    JWTAuthResponse authenticateStaff(LoginDto loginDto);
    JWTAuthResponse authenticateCustomer(LoginDto loginDto);
    StaffDto getStaffInfo();
    CustomerDto getCustomerInfo();
}
