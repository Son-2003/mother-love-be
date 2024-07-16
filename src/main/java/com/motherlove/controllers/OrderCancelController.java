package com.motherlove.controllers;

import com.motherlove.models.payload.requestModel.OrderCancelReq;
import com.motherlove.models.payload.responseModel.OrderCancelResponse;
import com.motherlove.services.IOrderCancelService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order-cancels")
@RequiredArgsConstructor
public class OrderCancelController {

    private final IOrderCancelService orderCancelService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @PostMapping
    public ResponseEntity<Object> addOrderCancel(@RequestBody OrderCancelReq orderCancelReq) {
        OrderCancelResponse orderCancelResponse = orderCancelService.saveOrderCancel(orderCancelReq);
        return new ResponseEntity<>(orderCancelResponse, HttpStatus.CREATED);
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<Object> getAllOrderCancel(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_ORDER_CANCEL_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(orderCancelService.getAllOrderCancel(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getAllOrderCancelByUserId(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_ORDER_CANCEL_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @PathVariable(name = "userId") Long userId
    ){
        return ResponseEntity.ok(orderCancelService.getAllOrderCancelOfMember(pageNo, pageSize, sortBy, sortDir, userId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/order-cancel/{orderCancelId}")
    public ResponseEntity<Object> getOrderCancelDetail(@PathVariable(name = "orderCancelId") Long orderCancelId){
        return ResponseEntity.ok(orderCancelService.getOrderCancel(orderCancelId));
    }
}
