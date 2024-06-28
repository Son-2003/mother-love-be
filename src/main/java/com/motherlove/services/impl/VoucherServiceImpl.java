package com.motherlove.services.impl;

import com.motherlove.models.entities.CustomerVoucher;
import com.motherlove.models.entities.User;
import com.motherlove.models.entities.Voucher;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.CustomerVoucherDto;
import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.repositories.CustomerVoucherRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.repositories.VoucherRepository;
import com.motherlove.services.VoucherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final ModelMapper mapper;

    @Override
    public Page<VoucherDto> getAllVouchers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Voucher> vouchers = voucherRepository.findVouchersValidAt(LocalDateTime.now(), pageable);

        return vouchers.map(this::mapToVoucherDto);
    }

    @Override
    public Page<CustomerVoucherDto> getAllVouchersOfMember(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<CustomerVoucher> vouchers = customerVoucherRepository.findVouchersOfMember(LocalDateTime.now(), userId, pageable);

        return vouchers.map(this::mapToCustomerDto);
    }

    @Override
    public Page<VoucherDto> getAllVouchersInManage(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Voucher> vouchers = voucherRepository.findAll(pageable);

        return vouchers.map(this::mapToVoucherDto);
    }

    @Override
    public VoucherDto addVoucher(VoucherDto voucherDto) {
        voucherDto.setStatus(true);
        Voucher voucher = mapper.map(voucherDto, Voucher.class);
        Voucher voucherDuplicate =  voucherRepository.findByVoucherCode(voucher.getVoucherCode());

        if(!voucher.getEndDate().isAfter(voucher.getStartDate())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher have StartDate is greater than EndDate!");
        }else if(voucherDuplicate != null){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This VoucherCode has already!");
        }else {
            Voucher savedVoucher = voucherRepository.save(voucher);
            return mapToVoucherDto(savedVoucher);
        }
    }

    @Override
    public VoucherDto getVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", id));
        return mapToVoucherDto(voucher);
    }


    @Override
    public VoucherDto updateVoucher(VoucherDto voucherDto) {
        Voucher voucherValidated = voucherRepository.findById(voucherDto.getVoucherId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", voucherDto.getVoucherId()));

        //Validate voucher
        if(voucherDto.getStartDate().isBefore(LocalDateTime.now())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher has already started!");
        }else if(!voucherDto.getEndDate().isAfter(voucherDto.getStartDate())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher have StartDate is greater than EndDate!");
        }else if(!voucherValidated.getVoucherCode().equalsIgnoreCase(voucherDto.getVoucherCode()) && voucherRepository.findByVoucherCode(voucherDto.getVoucherCode()) != null){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This VoucherCode has already!");
        }
        Voucher voucherUpdated = mapper.map(voucherDto, Voucher.class);
        return mapToVoucherDto(voucherRepository.save(voucherUpdated));
    }

    @Override
    public void deleteVoucher(long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", id));
        if (!voucher.getCustomerVouchers().isEmpty() || !voucher.getOrders().isEmpty()){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "There is at least one order, customer belongs to this voucher");
        }
        voucherRepository.delete(voucher);
    }

    @Override
    public CustomerVoucherDto addVoucherForUser(Long userId, Long voucherId) {
        //Find Voucher
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", voucherId));
        if(voucher.getEndDate().isBefore(LocalDateTime.now()) || voucher.getQuantity() == 0){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Voucher is not valid!");
        }
        //Check User duplicate voucher
        CustomerVoucher customerVoucherExist = customerVoucherRepository.findByVoucher_VoucherIdAndUser_UserId(voucherId, userId);
        if(customerVoucherExist != null){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "User already has this voucher!");
        }

        //Find User
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setUsed(false);
        customerVoucher.setAssignedDate(LocalDateTime.now());
        customerVoucher.setUsedDate(LocalDateTime.now());
        customerVoucher.setVoucher(voucher);
        customerVoucher.setUser(user);
        voucher.setQuantity(voucher.getQuantity() - 1);

        CustomerVoucher customerVoucherCreated = customerVoucherRepository.save(customerVoucher);
        return mapToCustomerDto(customerVoucherCreated);
    }

    private VoucherDto mapToVoucherDto(Voucher voucher) {
        return mapper.map(voucher, VoucherDto.class);
    }

    private CustomerVoucherDto mapToCustomerDto(CustomerVoucher customerVoucher) {
        return mapper.map(customerVoucher, CustomerVoucherDto.class);
    }
}
