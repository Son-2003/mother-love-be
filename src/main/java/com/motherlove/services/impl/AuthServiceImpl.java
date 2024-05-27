package com.motherlove.services.impl;

import com.motherlove.models.entities.Customer;
import com.motherlove.models.entities.Staff;
import com.motherlove.models.entities.UserType;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.CustomerDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.dto.StaffDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.repositories.CustomerRepository;
import com.motherlove.repositories.StaffRepository;
import com.motherlove.security.CustomUserDetailsService;
import com.motherlove.security.JwtTokenProvider;
import com.motherlove.services.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ModelMapper mapper;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, CustomerRepository customerRepository, StaffRepository staffRepository, JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.mapper = mapper;
    }

    @Override
    public JWTAuthResponse authenticateStaff(LoginDto loginDto) {
        try {
            customUserDetailsService.setUserType(UserType.STAFF);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUserNameOrEmailOrPhone(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new JWTAuthResponse(jwtTokenProvider.generateToken(authentication));
        }catch (AuthenticationException e){
            throw new MotherLoveApiException(HttpStatus.NOT_FOUND, "Username or password incorrect!");
        }
    }

    @Override
    public JWTAuthResponse authenticateCustomer(LoginDto loginDto) {
        try {
            customUserDetailsService.setUserType(UserType.CUSTOMER);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUserNameOrEmailOrPhone(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new JWTAuthResponse(jwtTokenProvider.generateToken(authentication));
        }catch (AuthenticationException e){
            throw new MotherLoveApiException(HttpStatus.NOT_FOUND, "Username or password incorrect!");
        }
    }

    @Override
    public StaffDto getStaffInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<Staff> user = Optional.ofNullable(staffRepository.findByStaffAccountOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Cannot find user's information!")));
        return mapToStaffDto(user);
    }

    @Override
    public CustomerDto getCustomerInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<Customer> user = Optional.ofNullable(customerRepository.findByCustomerAccountOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Cannot find user's information!")));
        return mapToCustomerDto(user);
    }

    private CustomerDto mapToCustomerDto(Optional<Customer> customer){
        return mapper.map(customer, CustomerDto.class);
    }
    private StaffDto mapToStaffDto(Optional<Staff> staff){
        return mapper.map(staff, StaffDto.class);
    }

}
