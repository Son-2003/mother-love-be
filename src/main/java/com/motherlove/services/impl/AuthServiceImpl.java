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
import com.motherlove.services.EmailService;
import com.motherlove.utils.AppConstants;
import com.motherlove.utils.AutomaticGeneratedPassword;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final EmailService emailService;

    @Override
    public JWTAuthResponse authenticateUser(LoginDto loginDto) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUserNameOrEmailOrPhone(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUserNameOrEmailOrPhone(loginDto.getUserNameOrEmailOrPhone(), loginDto.getUserNameOrEmailOrPhone(), loginDto.getUserNameOrEmailOrPhone()).orElseThrow(
                    () -> new ResourceNotFoundException("User")
            );
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);
            return new JWTAuthResponse(accessToken, refreshToken, "User login was successful");
    }

    @Override
    public JWTAuthResponse signupMember(SignupDto signupDto) {
        User user = setUpUser(signupDto);

        Role userRole = roleRepository.findByRoleName("ROLE_MEMBER")
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.BAD_REQUEST, "User Role not set."));
        user.setRole(userRole);
        user = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new JWTAuthResponse(accessToken, refreshToken,"User registration was successful");
    }

    @Override
    public JWTAuthResponse signupStaff(SignupDto signupDto) throws MessagingException {
        User user = setUpUser(signupDto);
        String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();

        Role userRole = roleRepository.findByRoleName("ROLE_STAFF")
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.BAD_REQUEST, "User Role not set."));
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setFirstLogin(true);
        user = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveUserToken(accessToken, refreshToken, user);

        String content = "<html>" +
                "<head>" +
                "<style>" +
                "table { width: 100%; border-collapse: collapse; }" +
                "th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }" +
                "th { background-color: #f2f2f2; }" +
                "body { font-family: Arial, sans-serif; }" +
                ".highlight { font-weight: bold; color: red; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<p class='greeting'>Hi, " + user.getFullName() + ",</p>" + // Apply the class to the greeting
                "<p>Tài khoản đăng nhập vào hệ thống MotherLove của bạn đã được tạo thành công.</p>" +
                "<p>Vui lòng truy cập hệ thống theo thông tin sau:</p>" +
                "<table>" +
                "<tr><th>Username</th><td>" + user.getEmail() + "</td></tr>" +
                "<tr><th>Password</th><td class='highlight'>" + randomPassword + "</td></tr>" +
                "</table>" +
                "<p><strong>Lưu ý:</strong> Vui lòng thay đổi mật khẩu sau khi đăng nhập.</p>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(user.getEmail(), "[MotherLove] - Tài khoản được tạo thành công", content);

        return new JWTAuthResponse(accessToken, refreshToken,"User registration was successful");
    }

    @Override
    public UserDto getCustomerInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByUserNameOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new ResourceNotFoundException("User")));
        return mapToCustomerDto(user);
    }

    @Override
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
//      extract the token from authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // extract username from token
        String username = jwtTokenProvider.getUsernameFromJwt(token);

        // check if the user exist in database
        User user = userRepository.findByUserNameOrEmailOrPhone(username, username, username)
                .orElseThrow(()->new RuntimeException("No user found"));

        // check if the token is valid
        if(jwtTokenProvider.isValidRefreshToken(token, user.getUserName())) {
            // generate access token
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new JWTAuthResponse(accessToken, refreshToken, "New token generated"), HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNameOrEmailOrPhone(username, username, username)
                .orElseThrow(() -> new MotherLoveApiException(HttpStatus.NOT_FOUND, "User cannot found!"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Old password does not match!");
        }
        if(!newPassword.matches(AppConstants.PASSWORD_REGEX))
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST,
                    "Password must be 8-12 characters with at least one uppercase letter, one number, and one special character (!@#$%^&*).");
        user.setPassword(passwordEncoder.encode(newPassword));
        if(user.isFirstLogin()) user.setFirstLogin(false);
        userRepository.save(user);
    }


    private User setUpUser(SignupDto signupDto) {
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
        user.setFullName(signupDto.getFullName());
        user.setPassword(signupDto.getPassword());
        user.setPhone(signupDto.getPhone());
        user.setGender(signupDto.getGender());
        user.setImage("https://res.cloudinary.com/dpysbryyk/image/upload/v1717827115/Milk/UserDefault/dfzhxjcbnixmp8aybnge.jpg");
        user.setFirstLogin(false);
        user.setStatus(1);
        user.setPoint(0);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());

        return user;
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
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
