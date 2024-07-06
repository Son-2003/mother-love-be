package com.motherlove.controllers;

import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import com.motherlove.services.IOrderService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<Object> getAllOrder(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_ORDER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(orderService.getAllOrder(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/search")
    public ResponseEntity<Object> searchOrders(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_ORDER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(value = "orderDateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime orderDateFrom,
            @RequestParam(value = "orderDateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime orderDateTo,
            @RequestParam(value = "status", required = false) List<OrderStatus> status,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        Map<String, Object> searchParams = new HashMap<>();

        if (status != null && !status.isEmpty()) searchParams.put("status", status);
        if (orderDateFrom != null) searchParams.put("orderDateFrom", orderDateFrom);
        if (orderDateTo != null) searchParams.put("orderDateTo", orderDateTo);
        if (fullName != null) searchParams.put("fullName", fullName);
        if (userName != null) searchParams.put("userName", userName);

        return ResponseEntity.ok(orderService.searchOrder(pageNo, pageSize, sortBy, sortDir, searchParams, userId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getAllOrderByUserId(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_ORDER_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @PathVariable(name = "userId") Long userId
    ){
        return ResponseEntity.ok(orderService.getAllOrderByCustomerId(pageNo, pageSize, sortBy, sortDir, userId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Object> getOrderDetail(@PathVariable(name = "orderId") Long orderId){
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @PutMapping("/{orderId}")
    public ResponseEntity<Object> updateStatusPaymentSuccess(@PathVariable(name = "orderId") Long orderId){
        return ResponseEntity.ok(orderService.updateStatusPaymentSuccess(orderId));
    }

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @PostMapping
    public ResponseEntity<Object> addOrder(@RequestBody List<CartItem> cartItems, @RequestParam Long voucherId, @RequestParam Long userId, @RequestParam Long addressId, @RequestParam boolean isPreOrder) {
        OrderResponse orderResponse = orderService.createOrder(cartItems, userId, addressId, voucherId, isPreOrder);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }


}
