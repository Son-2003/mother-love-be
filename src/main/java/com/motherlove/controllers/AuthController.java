package com.motherlove.controllers;

import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.dto.SignupDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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

    @PostMapping(value = "/register")
    public ResponseEntity<Object> signup(@Valid@RequestBody SignupDto signupDto){
        JWTAuthResponse response = authService.signupMember(signupDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
