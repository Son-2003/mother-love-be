package com.motherlove.controllers;

import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.dto.SignupDto;
import com.motherlove.models.payload.requestModel.PasswordChangeReq;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.services.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @Operation(
            summary = "Login User",
            description = "Login user by UserName, Email, Phone"
    )
    @PostMapping("/user/login")
    public ResponseEntity<Object> authenticationUser(@Valid @RequestBody LoginDto loginDto){
            JWTAuthResponse jwtAuthResponse = authService.authenticateUser(loginDto);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtAuthResponse.getAccessToken());
            return ResponseEntity.ok(jwtAuthResponse);
    }

    @Operation(
            summary = "Sign Up Account Member"
    )
    @PostMapping(value = "/register/member")
    public ResponseEntity<Object> signupMember(@Valid @RequestBody SignupDto signupDto){
        JWTAuthResponse response = authService.signupMember(signupDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Sign Up Account Staff"
    )
    @PostMapping(value = "/register/staff")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> signupStaff(@Valid @RequestBody SignupDto signupDto) throws MessagingException {
        JWTAuthResponse response = authService.signupStaff(signupDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeReq request) {
        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Generate AccessToken and Refresh Token"
    )
    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authService.refreshToken(request, response);
    }

    @Operation(
            summary = "Get Info User"
    )
    @SecurityRequirement(name = "Bear Authentication")
    @GetMapping("/user/info")
    public ResponseEntity<Object> getInfo() {
        return ResponseEntity.ok(authService.getCustomerInfo());
    }
}
