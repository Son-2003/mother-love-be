package com.motherlove.controllers;

import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import com.motherlove.services.IOrderService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(name = "sortBy", defaultValue = "categoryId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(orderService.getAllOrder(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getAllOrderByUserId(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "categoryId", required = false) String sortBy,
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

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @PostMapping
    public ResponseEntity<Object> addOrder(@RequestBody List<CartItem> cartItems, @RequestParam Long voucherId, @RequestParam Long userId, @RequestParam Long addressId) {
        OrderResponse orderResponse = orderService.createOrder(cartItems, userId, addressId, voucherId);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}
