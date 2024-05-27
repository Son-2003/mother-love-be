package com.motherlove.controllers;

import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.payload.dto.ProductDto;
import com.motherlove.services.ProductService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get List Products",
            description = "Get List Products"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try{
            Page<ProductDto> classes = productService.getAllProducts(page, size);
            return ResponseEntity.ok().body(classes);
        }catch (MotherLoveApiException e){
            return ResponseEntity.status(e.getStatus()).body(
                    Map.of(AppConstants.STATUS, e.getStatus().value(), AppConstants.MESSAGE, e.getMessage())
            );
        }
    }
}
