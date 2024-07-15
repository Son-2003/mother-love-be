package com.motherlove.controllers;

import com.motherlove.models.payload.dto.FeedbackDto;
import com.motherlove.models.payload.requestModel.FeedbackCreateReq;
import com.motherlove.models.payload.requestModel.FeedbackUpdateReq;
import com.motherlove.models.payload.responseModel.FeedbackResponse;
import com.motherlove.services.IFeedbackService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@Validated
public class FeedbackController {
    private final IFeedbackService feedbackService;

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PostMapping
    public ResponseEntity<List<FeedbackDto>> createFeedback(@RequestBody @Valid List<@Valid FeedbackCreateReq> feedbackCreateReqs, @RequestParam Long userId, @RequestParam Long orderId) {
        List<FeedbackDto> savedFeedback = feedbackService.createFeedback(feedbackCreateReqs, userId, orderId);
        return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> getAllFeedback(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY_FEEDBACK_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(feedbackService.viewFeedbackInManage(pageNo, pageSize, sortBy, sortDir));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/product/{productId}")
    public ResponseEntity<FeedbackResponse> getFeedbacksOfProduct(@PathVariable long productId){
        return ResponseEntity.ok(feedbackService.viewFeedback(productId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/search/{productId}")
    public ResponseEntity<Object> searchFeedback(
            @RequestParam(value = "rating", required = false) List<Integer> rating,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "brandName", required = false) List<String> brandName,
            @RequestParam(value = "categoryName", required = false) List<String> categoryName,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "userName", required = false) String userName,
            @PathVariable(value = "productId") Long productId
    ) {
        Map<String, Object> searchParams = new HashMap<>();

        if (rating != null && !rating.isEmpty()) searchParams.put("rating", rating);
        if (productName != null) searchParams.put("productName", productName);
        if (brandName != null) searchParams.put("brandName", brandName);
        if (categoryName != null) searchParams.put("categoryName", categoryName);
        if (fullName != null) searchParams.put("fullName", fullName);
        if (userName != null) searchParams.put("userName", userName);

        return ResponseEntity.ok(feedbackService.searchFeedback(searchParams, productId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbacksOfOrder(@PathVariable long orderId){
        return ResponseEntity.ok(feedbackService.viewFeedbackOfOrder(orderId));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @PutMapping()
    public ResponseEntity<FeedbackDto> updateFeedback(@RequestBody @Valid FeedbackUpdateReq feedbackUpdateReq){
        return ResponseEntity.ok(feedbackService.updateFeedback(feedbackUpdateReq));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "feedbackId") long feedbackId){
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok("Feedback deleted successfully!");
    }
}
