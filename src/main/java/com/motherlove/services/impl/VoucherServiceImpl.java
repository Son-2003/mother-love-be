package com.motherlove.services.impl;

import com.motherlove.models.entities.Voucher;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.VoucherDto;
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
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final ModelMapper mapper;

    @Override
    public Page<VoucherDto> getAllVouchers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Voucher> vouchers = voucherRepository.findAll(pageable);

        return vouchers.map(this::mapToDto);
    }

    @Override
    public VoucherDto addVoucher(VoucherDto voucherDto) {
        Voucher voucher = mapper.map(voucherDto, Voucher.class);
        Voucher voucherDuplicate =  voucherRepository.findByVoucherCode(voucher.getVoucherCode());

        if(!voucher.getEndDate().isAfter(voucher.getStartDate())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher have StartDate is greater than EndDate!");
        }else if(voucherDuplicate != null){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This VoucherCode has already!");
        }else {
            Voucher savedVoucher = voucherRepository.save(voucher);
            return mapToDto(savedVoucher);
        }
    }

    @Override
    public VoucherDto getVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", id));
        return mapToDto(voucher);
    }

    @Override
    public VoucherDto updateVoucher(VoucherDto voucherDto) {
        voucherRepository.findById(voucherDto.getVoucherId()).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", voucherDto.getVoucherId()));

        //List voucher not contain voucherCode with id in VoucherDto
        List<Voucher> vouchers =  voucherRepository.findAll().stream().filter(voucher -> !Objects.equals(voucher.getVoucherId(), voucherDto.getVoucherId())).toList();
        if(voucherDto.getStartDate().isBefore(LocalDateTime.now())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher has already started!");
        }if(!voucherDto.getEndDate().isAfter(voucherDto.getStartDate())){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher have StartDate is greater than EndDate!");
        }else if(vouchers.stream().anyMatch(voucher -> voucher.getVoucherCode().equalsIgnoreCase(voucherDto.getVoucherCode()))){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This VoucherCode has already!");
        }else {
            Voucher voucher = mapper.map(voucherDto, Voucher.class);
            return mapToDto(voucherRepository.save(voucher));
        }
    }

    @Override
    public void deleteVoucher(long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Voucher", "ID", id));
        if (!voucher.getCustomerVouchers().isEmpty() || !voucher.getOrderVouchers().isEmpty()){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "There is at least one product belongs to this category");
        }
        voucherRepository.delete(voucher);
    }

    private VoucherDto mapToDto(Voucher voucher) {
        return mapper.map(voucher, VoucherDto.class);
    }
}
