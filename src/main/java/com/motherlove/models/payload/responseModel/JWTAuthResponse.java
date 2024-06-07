package com.motherlove.models.payload.responseModel;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTAuthResponse {
    private String accessToken;
    private String message;
    private String tokenType = "Bearer";
    public JWTAuthResponse(String accessToken, String message) {
        this.message = message;
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
