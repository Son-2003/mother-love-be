package com.motherlove.models.payload.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private Long supplierId;

    @NotEmpty(message = "Supllier's name cannot be blank")
    @Size(min = 2, message = "Supllier's name must have at least 2 characters")
    private String supplierName;

    @NotEmpty(message = "Supllier's contact info cannot be blank")
    @Size(min = 2, message = "Supllier's contact info must have at least 2 characters")
    private String contactInfo;

    @NotEmpty(message = "Supllier's address cannot be blank")
    @Size(min = 2, message = "Supllier's address must have at least 2 characters")
    private String address;

    @NotEmpty(message = "Supllier's email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Supllier's phone cannot be blank")
    @Size(min = 2, message = "Supllier's phone must have at least 2 characters")
    private String phone;
}
