package com.motherlove.services.impl;

import com.motherlove.models.entities.Address;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.AddressDto;
import com.motherlove.repositories.AddressRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Address> getAllAddresses(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return addressRepository.findAll(pageable);
    }

    @Override
    public Page<Address> getAllAddressesByUserId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return addressRepository.findByUser_UserId(userId, pageable);
    }

    @Override
    public Address getAddressById(long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Address.class.getName(), "ID", id));
    }

    @Override
    public Address setAddressDefault(long userId, long addressOldId, long addressNewId) {
        Optional<Address> addressOldDefault = Optional.ofNullable(addressRepository.findByUser_UserIdAndAddressId(userId, addressOldId)
                .orElseThrow(() -> new ResourceNotFoundException(Address.class.getName(), "ID", addressOldId)));

        Optional<Address> addressNewDefault = Optional.ofNullable(addressRepository.findByUser_UserIdAndAddressId(userId, addressNewId)
                .orElseThrow(() -> new ResourceNotFoundException(Address.class.getName(), "ID", addressNewId)));
        if(addressOldDefault.isPresent() && addressNewDefault.isPresent()){
            addressOldDefault.get().setDefault(false);
            addressNewDefault.get().setDefault(true);
            addressRepository.save(addressOldDefault.get());
            addressRepository.save(addressNewDefault.get());
        }
        return addressNewDefault.get();
    }

    @Override
    public Address addAddress(AddressDto addressDto) {
        Address address = mapToDto(addressDto);
        address.setAddressId(null);

        List<Address> addressPage = addressRepository.findByUser_UserId(address.getUser().getUserId());
        if(addressPage.isEmpty()){
            address.setDefault(true);
        }
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(AddressDto addressDto) {
        if (!addressRepository.existsById(addressDto.getAddressId())) {
            throw new ResourceNotFoundException(Address.class.getName(), "ID", addressDto.getAddressId());
        }
        return addressRepository.save(mapToDto(addressDto));
    }

    @Override
    public Address deleteAddress(long id) {
        Address address = getAddressById(id);
        addressRepository.delete(address);
        return address;
    }

    public Address mapToDto(AddressDto addressDto){
        User user = userRepository.findById(addressDto.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getName(), "ID", addressDto.getUser().getUserId()));
        Address address = new Address();
        address.setAddressId(addressDto.getAddressId());
        address.setAddressLine(addressDto.getAddressLine());
        address.setDistrict(addressDto.getDistrict());
        address.setCity(addressDto.getCity());
        address.setUser(user);
        address.setDefault(false);
        return address;
    }
}
