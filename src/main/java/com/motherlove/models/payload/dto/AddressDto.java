package com.motherlove.models.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long addressId;
    private String addressLine;
    private String district;
    private String city;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private UserDto user;
}
