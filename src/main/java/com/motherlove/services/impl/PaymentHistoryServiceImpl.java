package com.motherlove.services.impl;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.PaymentHistory;
import com.motherlove.models.entities.PaymentMethod;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.PaymentHistoryDto;
import com.motherlove.models.payload.dto.PaymentMethodDto;
import com.motherlove.models.payload.responseModel.CustomPaymentHistoryResponse;
import com.motherlove.models.payload.responseModel.PaymentHistoryResponse;
import com.motherlove.repositories.OrderRepository;
import com.motherlove.repositories.PaymentHistoryRepository;
import com.motherlove.repositories.PaymentMethodRepository;
import com.motherlove.services.IPaymentHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentHistoryServiceImpl implements IPaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentHistoryServiceImpl(PaymentHistoryRepository paymentHistoryRepository, PaymentMethodRepository paymentMethodRepository, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomPaymentHistoryResponse addPaymentHistory(PaymentHistoryDto paymentHistoryDto) {
        PaymentHistory paymentHistory = modelMapper.map(paymentHistoryDto, PaymentHistory.class);
        paymentHistory.setPaymentMethod(findPaymentMethodById(paymentHistoryDto.getPaymentMethodId()));
        paymentHistory.setOrder(findOrderById(paymentHistoryDto.getOrderId()));

        if (paymentHistoryRepository.findPaymentHistoryByOrderId(paymentHistoryDto.getOrderId()) != null) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Not allowed duplicated payment!");
        }

        PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);
        return createCustomPaymentHistoryResponse(savedPaymentHistory);
    }

    @Override
    public CustomPaymentHistoryResponse getPaymentHistory(Long id) {
        PaymentHistory paymentHistory = findPaymentHistoryById(id);
        return createCustomPaymentHistoryResponse(paymentHistory);
    }

    @Override
    public PaymentHistoryResponse getAllPaymentHistories(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<PaymentHistory> data = paymentHistoryRepository.findAll(pageable);

        //get content of page
        List<PaymentHistory> paymentHistoryList = data.getContent();

        //format the response
        List<PaymentHistoryDto> content = paymentHistoryList.stream().map(paymentHistory -> modelMapper.map(paymentHistory, PaymentHistoryDto.class)).collect(Collectors.toList());
        PaymentHistoryResponse paymentHistoryResponse = new PaymentHistoryResponse();
        paymentHistoryResponse.setContent(content);
        paymentHistoryResponse.setPageNo(data.getNumber());
        paymentHistoryResponse.setPageSize(data.getSize());
        paymentHistoryResponse.setTotalElements(data.getTotalElements());
        paymentHistoryResponse.setTotalPages(data.getTotalPages());
        paymentHistoryResponse.setLast(data.isLast());

        return paymentHistoryResponse;
    }

    @Override
    public CustomPaymentHistoryResponse updatePaymentHistory(PaymentHistoryDto paymentHistoryDto, long paymentHistoryId) {
        PaymentHistory paymentHistory = findPaymentHistoryById(paymentHistoryId);
        paymentHistory.setPaymentMethod(findPaymentMethodById(paymentHistoryDto.getPaymentMethodId()));
        paymentHistory.setOrder(findOrderById(paymentHistoryDto.getOrderId()));
        paymentHistory.setAmount(paymentHistoryDto.getAmount());
        paymentHistory.setStatus(paymentHistoryDto.getStatus());

        if (paymentHistoryRepository.findPaymentHistoryByOrderId(paymentHistoryDto.getOrderId()) != null) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Not allowed duplicated payment!");
        }

        PaymentHistory updatedPaymentHistory = paymentHistoryRepository.save(paymentHistory);
        return createCustomPaymentHistoryResponse(updatedPaymentHistory);
    }

    @Override
    public void deletePaymentHistory(long id) {
        PaymentHistory paymentHistory = findPaymentHistoryById(id);
        paymentHistoryRepository.delete(paymentHistory);
    }

    private PaymentMethod findPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    private PaymentHistory findPaymentHistoryById(Long id) {
        return paymentHistoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PaymentHistory", "id", id));
    }

    private CustomPaymentHistoryResponse createCustomPaymentHistoryResponse(PaymentHistory paymentHistory) {
        CustomPaymentHistoryResponse response = new CustomPaymentHistoryResponse();
        response.setPaymentHistoryId(paymentHistory.getPaymentHistoryId());
        response.setAmount(paymentHistory.getAmount());
        response.setStatus(paymentHistory.getStatus());
        response.setCreatedDate(paymentHistory.getCreatedDate());
        response.setPaymentMethod(modelMapper.map(paymentHistory.getPaymentMethod(), PaymentMethodDto.class));
        response.setOrder(modelMapper.map(paymentHistory.getOrder(), OrderDto.class));
        return response;
    }
}
