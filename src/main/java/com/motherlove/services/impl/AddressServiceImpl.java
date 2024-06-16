package com.motherlove.services.impl;

import com.motherlove.models.entities.Address;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.AddressDto;
import com.motherlove.repositories.AddressRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;



    @Override
    public Page<Address> getAllAddresses(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

       return addressRepository.findAll(pageable);
    }

    @Override
    public Address getAddressById(long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Address.class.getName(), "ID", id));
    }

    @Override
    public Address addAddress(AddressDto addressDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getName(), "ID", userId));

        addressDto.setAddressId(null);
        Address address = mapper.map(addressDto, Address.class);
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(AddressDto addressDto) {
        if (!addressRepository.existsById(addressDto.getAddressId())) {
            throw new ResourceNotFoundException(Address.class.getName(), "ID", addressDto.getAddressId());
        }
        Address address = mapper.map(addressDto, Address.class);
        return addressRepository.save(address);
    }

    @Override
    public Address deleteAddress(long id) {
        Address address = getAddressById(id);
        addressRepository.delete(address);
        return address;
    }


}
