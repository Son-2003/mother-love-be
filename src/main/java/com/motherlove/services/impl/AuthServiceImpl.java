package com.motherlove.services.impl;

import com.motherlove.models.entities.Role;
import com.motherlove.models.entities.Token;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.SignupDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.repositories.RoleRepository;
import com.motherlove.repositories.TokenRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.security.JwtTokenProvider;
import com.motherlove.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    public JWTAuthResponse authenticateUser(LoginDto loginDto) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUserNameOrEmailOrPhone(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUserNameOrEmailOrPhone(loginDto.getUserNameOrEmailOrPhone(), loginDto.getUserNameOrEmailOrPhone(), loginDto.getUserNameOrEmailOrPhone()).orElseThrow(
                    () -> new ResourceNotFoundException("User")
            );
            revokeAllTokenByUser(user);
            saveUserToken(jwtTokenProvider.generateToken(authentication), user);
            return new JWTAuthResponse(jwtTokenProvider.generateToken(authentication), "User login was successful");
    }

    @Override
    public JWTAuthResponse signupMember(SignupDto signupDto) {
        // add check if username already exists
        if (userRepository.existsByUserName(signupDto.getUsername())) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Username is already exist!");
        }

        // add check if email already exists
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Email is already exist!");
        }

        User user = new User();
        user.setUserName(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setFullName(signupDto.getFullName());
        user.setPhone(signupDto.getPhone());
        user.setGender(signupDto.getGender());
        user.setImage("https://res.cloudinary.com/dpysbryyk/image/upload/v1717827115/Milk/UserDefault/dfzhxjcbnixmp8aybnge.jpg");
        user.setStatus(1);
        user.setPoint(0);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());

        Role userRole = roleRepository.findByRoleName("ROLE_MEMBER")
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.BAD_REQUEST, "User Role not set."));
        user.setRole(userRole);

        user = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupDto.getUsername(), signupDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);

        saveUserToken(accessToken, user);

        return new JWTAuthResponse(accessToken,"User registration was successful");
    }

    @Override
    public UserDto getCustomerInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByUserNameOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new ResourceNotFoundException("User")));
        return mapToCustomerDto(user);
    }

    private void saveUserToken(String accessToken, User user) {
        Token token = new Token();
        token.setToken(accessToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllByUser_UserId(user.getUserId());
        if(validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t-> t.setLoggedOut(true));

        tokenRepository.saveAll(validTokens);
    }

    private UserDto mapToCustomerDto(Optional<User> customer){
        return mapper.map(customer, UserDto.class);
    }
}
