package com.motherlove.controllers;

import com.motherlove.models.payload.responseModel.VNPayResponse;
import com.motherlove.services.IPaymentHistoryService;
import com.motherlove.services.IVNPayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
public class VNPayPaymentController {
    private final IVNPayPaymentService vnPayPaymentService;
    @GetMapping("/vn-pay")
    public ResponseEntity<VNPayResponse> pay(HttpServletRequest request) {
        return ResponseEntity.ok(vnPayPaymentService.createVNPayPayment(request));
    }
    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        vnPayPaymentService.handleVNPayResponse(request);
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            response.sendRedirect("https://motherlove.onrender.com/success");
        } else {
            response.sendRedirect("https://motherlove.onrender.com/fail");
        }
    }
}
