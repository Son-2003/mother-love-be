package com.motherlove.controllers;

import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.dto.LoginDto;
import com.motherlove.models.payload.responseModel.JWTAuthResponse;
import com.motherlove.services.AuthService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Object> loginStaff(@RequestBody @Valid LoginDto loginDto){
        try {
            JWTAuthResponse jwtAuthResponse = authService.authenticateUser(loginDto);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtAuthResponse.getAccessToken());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jwtAuthResponse);
        }catch (MotherLoveApiException e){
            return ResponseEntity.status(e.getStatus()).body(
                    Map.of(AppConstants.STATUS, e.getStatus().value(), AppConstants.MESSAGE, e.getMessage())
            );
        }
    }

    @Operation(
            summary = "Get Info User"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @GetMapping("/user/info")
    public ResponseEntity<Object> getCustomerInfo() {
        try {
            UserDto userDto = authService.getCustomerInfo();
            return ResponseEntity.ok().body(userDto);
        }catch (MotherLoveApiException e){
            return ResponseEntity.status(e.getStatus()).body(
                    Map.of(AppConstants.STATUS, e.getStatus().value(), AppConstants.MESSAGE, e.getMessage())
            );
        }
    }
}
