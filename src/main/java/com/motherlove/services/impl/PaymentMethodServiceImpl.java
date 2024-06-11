package com.motherlove.services.impl;

import com.motherlove.models.entities.PaymentMethod;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.PaymentMethodDto;
import com.motherlove.models.payload.responseModel.PaymentMethodResponse;
import com.motherlove.repositories.PaymentMethodRepository;
import com.motherlove.services.PaymentMethodService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private PaymentMethodRepository paymentMethodRepository;
    private ModelMapper modelMapper;
    
    @Autowired
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository, ModelMapper modelMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaymentMethodDto addPaymentMethod(PaymentMethodDto paymentMethodDto) {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDto, PaymentMethod.class);
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return modelMapper.map(savedPaymentMethod, PaymentMethodDto.class);
    }

    @Override
    public PaymentMethodDto getPaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));
        return modelMapper.map(paymentMethod, PaymentMethodDto.class);
    }

    @Override
    public PaymentMethodResponse getAllPaymentMethods(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<PaymentMethod> posts = paymentMethodRepository.findAll(pageable);

        //get content of page
        List<PaymentMethod> paymentMethodList = posts.getContent();

        //format the response
        List<PaymentMethodDto> content = paymentMethodList.stream().map(paymentMethod -> modelMapper.map(paymentMethod, PaymentMethodDto.class)).collect(Collectors.toList());
        PaymentMethodResponse paymentMethodResponse = new PaymentMethodResponse();
        paymentMethodResponse.setContent(content);
        paymentMethodResponse.setPageNo(posts.getNumber());
        paymentMethodResponse.setPageSize(posts.getSize());
        paymentMethodResponse.setTotalElements(posts.getTotalElements());
        paymentMethodResponse.setTotalPages(posts.getTotalPages());
        paymentMethodResponse.setLast(posts.isLast());

        return paymentMethodResponse;
    }

    @Override
    public PaymentMethodDto updatePaymentMethod(PaymentMethodDto paymentMethodDto, long paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", paymentMethodId));
        paymentMethod.setMethodName(paymentMethodDto.getMethodName());
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return modelMapper.map(updatedPaymentMethod, PaymentMethodDto.class);
    }

    @Override
    public void deletePaymentMethod(long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));
        paymentMethodRepository.delete(paymentMethod);
    }
}
