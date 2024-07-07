package com.motherlove.services.impl;

import com.motherlove.config.VNPayConfig;
import com.motherlove.models.entities.Order;
import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.enums.PaymentHistoryStatus;
import com.motherlove.models.payload.dto.PaymentHistoryDto;
import com.motherlove.models.payload.responseModel.VNPayResponse;
import com.motherlove.services.IOrderService;
import com.motherlove.services.IPaymentHistoryService;
import com.motherlove.services.IVNPayPaymentService;
import com.motherlove.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class VNPayPaymentServiceImpl implements IVNPayPaymentService {
    private final VNPayConfig vnPayConfig;
    private final IPaymentHistoryService paymentHistoryService;
    private final IOrderService orderService;
    @Override
    public VNPayResponse createVNPayPayment(HttpServletRequest request) {
        String bankCode = request.getParameter("bankCode");
        String orderId = request.getParameter("orderId");
        Order order = orderService.getOrderByOrderId(Long.parseLong(orderId));
        long amount = (long) (order.getAfterTotalAmount() * 100L);
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_OrderInfo", orderId);
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Override
    public void handleVNPayResponse(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
                .amount(Float.parseFloat(request.getParameter("vnp_Amount")) / 100)
                .status(status.equals("00") ? PaymentHistoryStatus.SUCCESS : PaymentHistoryStatus.FAILED)
                .orderId(Long.parseLong(request.getParameter("vnp_OrderInfo")))
                .paymentMethodId(1L)
                .build();
        paymentHistoryService.addPaymentHistory(paymentHistoryDto);
        orderService.updateOrderStatus(paymentHistoryDto.getOrderId(), status.equals("00") ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED);
    }
}
