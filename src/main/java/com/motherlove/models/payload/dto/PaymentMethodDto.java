package com.motherlove.models.payload.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {
    private Long paymentMethodId;

    @NotEmpty(message = "Payment method's name cannot be blank")
    @Size(min = 2, message = "Payment method's name must have at least 2 characters")
    private String methodName;
}
