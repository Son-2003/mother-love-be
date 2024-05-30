package com.motherlove.services.impl;

import com.motherlove.models.entities.User;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.repositories.UserRepository;
import com.motherlove.security.JwtTokenProvider;
import com.motherlove.services.AuthService;
import org.modelmapper.ModelMapper;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper mapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mapper = mapper;
    }

    @Override
    public JWTAuthResponse authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUserNameOrEmailOrPhone(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new JWTAuthResponse(jwtTokenProvider.generateToken(authentication));
        }catch (AuthenticationException e){
            throw new MotherLoveApiException(HttpStatus.NOT_FOUND, "Username or password incorrect!");
        }
    }

    @Override
    public UserDto getCustomerInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByUserNameOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "Cannot find user's information!")));
        return mapToCustomerDto(user);
    }

    private UserDto mapToCustomerDto(Optional<User> customer){
        return mapper.map(customer, UserDto.class);
    }
}
