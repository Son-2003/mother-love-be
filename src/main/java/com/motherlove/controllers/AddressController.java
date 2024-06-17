package com.motherlove.controllers;

import com.motherlove.models.entities.Address;
import com.motherlove.models.payload.dto.AddressDto;
import com.motherlove.services.AddressService;
import com.motherlove.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    private final ModelMapper mapper;

    private AddressDto mapEntityToDto(Address address) {
        return mapper.map(address, AddressDto.class);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getAllAddress(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "addressId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        var list = addressService.getAllAddresses(pageNo, pageSize, sortBy, sortDir);
        List<AddressDto> result = list.stream().map(this::mapEntityToDto).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<Object> getAllAddressByUserId(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "addressId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "userId") Long userId
    ) {
        var list = addressService.getAllAddressesByUserId(pageNo, pageSize, sortBy, sortDir, userId);
        List<AddressDto> result = list.stream().map(this::mapEntityToDto).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<AddressDto> getAddress(@PathVariable long id) {
        return ResponseEntity.ok(mapEntityToDto(addressService.getAddressById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<AddressDto> addAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(mapEntityToDto(addressService.addAddress(addressDto)));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<AddressDto> updateAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(mapEntityToDto(addressService.updateAddress(addressDto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<AddressDto> deleteAddress(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok(mapEntityToDto(addressService.deleteAddress(id)));
    }


}
