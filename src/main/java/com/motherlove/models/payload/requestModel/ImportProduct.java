package com.motherlove.models.payload.requestModel;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportProduct {
    @Min(value = 50, message = "Quantity must be at least 50")
    private int quantity;
    @Min(value = 1, message = "SupplierId must be at least 1")
    private Long supplierId;
    @Min(value = 1, message = "ProductId must be at least 1")
    private Long productId;
}
