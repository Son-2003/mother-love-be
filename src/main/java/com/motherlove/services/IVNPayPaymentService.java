package com.motherlove.services;

import com.motherlove.models.payload.responseModel.VNPayResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IVNPayPaymentService {
    VNPayResponse createVNPayPayment(HttpServletRequest request);
}
