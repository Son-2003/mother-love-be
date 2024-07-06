package com.motherlove.models.payload.responseModel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayResponse {
    public String code;
    public String message;
    public String paymentUrl;
}
