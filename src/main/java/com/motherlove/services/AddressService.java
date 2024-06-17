package com.motherlove.services;

import com.motherlove.models.entities.Address;
import com.motherlove.models.payload.dto.AddressDto;
import org.springframework.data.domain.Page;

public interface AddressService {

    Page<Address> getAllAddresses(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<Address> getAllAddressesByUserId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    Address getAddressById(long id);

    Address addAddress(AddressDto addressDto, Long userId);

    Address updateAddress(AddressDto addressDto);

    Address deleteAddress(long id);
}
