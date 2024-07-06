package com.motherlove.controllers;

import com.motherlove.models.payload.responseModel.VNPayResponse;
import com.motherlove.services.IVNPayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<VNPayResponse> payCallbackHandler(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String status = request.getParameter("vnp_ResponseCode");
        // response.sendRedirect("FE_URL"), FE check if response code is 00 => success
        // Save payment history
        // Update order status
        if (status.equals("00")) {
            return ResponseEntity.ok(new VNPayResponse("00", "Success", ""));
        } else {
            return ResponseEntity.badRequest().body(new VNPayResponse("99", "Failed", ""));
        }
    }
}
