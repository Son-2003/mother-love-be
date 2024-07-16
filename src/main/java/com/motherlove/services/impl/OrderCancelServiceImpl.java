package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.OrderCancelDto;
import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.models.payload.requestModel.OrderCancelReq;
import com.motherlove.models.payload.responseModel.GiftResponse;
import com.motherlove.models.payload.responseModel.OrderCancelResponse;
import com.motherlove.models.payload.responseModel.ProductOrderDetailResponse;
import com.motherlove.repositories.*;
import com.motherlove.services.IOrderCancelService;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCancelServiceImpl implements IOrderCancelService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final OrderCancelRepository orderCancelRepository;
    private final ModelMapper mapper;

    @Override
    public OrderCancelResponse saveOrderCancel(OrderCancelReq orderCancelReq) {
        Order order = orderRepository.findById(orderCancelReq.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order"));
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());
        CustomerVoucher customerVoucher = customerVoucherRepository.findCustomerVoucherExist(order.getVoucher().getVoucherId(), order.getUser().getUserId()).stream().findFirst().orElse(null);

        OrderCancel orderCancel = new OrderCancel();
        if (order.getStatus().equals(OrderStatus.PRE_ORDER) || order.getStatus().equals(OrderStatus.PENDING)) {
            order.setStatus(OrderStatus.CANCELLED);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = productRepository.findById(orderDetail.getProduct().getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product"));
                product.setQuantityProduct(product.getQuantityProduct() + orderDetail.getQuantity());
            }

            if (customerVoucher.isUsed()) {
                customerVoucher.setUsed(false);
                customerVoucher.setQuantityAvailable(customerVoucher.getQuantityAvailable() + 1);
            } else {
                customerVoucher.setQuantityAvailable(customerVoucher.getQuantityAvailable() + 1);
            }

            orderCancel.setOrder(order);
            orderCancel.setReason(orderCancelReq.getReason());
            orderCancel.setCancelDate(LocalDateTime.now());
        }else {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Cannot cancel this order!");
        }

        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        customerVoucherRepository.save(customerVoucher);
        return mapOrderToOrderResponse(orderCancelRepository.save(orderCancel));
    }

    @Override
    public Page<OrderCancelResponse> getAllOrderCancel(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<OrderCancel> orderCancelsPage = orderCancelRepository.findAll(pageable);

        return orderCancelsPage.map(this::mapOrderToOrderResponse);
    }

    @Override
    public Page<OrderCancelResponse> getAllOrderCancelOfMember(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<OrderCancel> orderCancelsPage = orderCancelRepository.findByOrder_User_UserId(userId, pageable);

        return orderCancelsPage.map(this::mapOrderToOrderResponse);
    }

    @Override
    public OrderCancelResponse getOrderCancel(Long cancelId) {
        OrderCancel orderCancel = orderCancelRepository.findById(cancelId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Cancel"));

        return mapOrderToOrderResponse(orderCancel);
    }

    private OrderDto mapToOrderDto(Order order){
        return mapper.map(order, OrderDto.class);
    }

    private OrderCancelDto mapToOrderCancelDto(OrderCancel orderCancel){
        return mapper.map(orderCancel, OrderCancelDto.class);
    }

    private List<OrderDetailDto> mapToOrderDetailDto(OrderCancel orderCancel){
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(orderCancel.getOrder().getOrderId());
        List<OrderDetailDto> orderDetailDTOs = new ArrayList<>();
        for (OrderDetail tmp: orderDetails){
            OrderDetailDto orderDetailDto = mapper.map(tmp, OrderDetailDto.class);
            ProductOrderDetailResponse productResponse = new ProductOrderDetailResponse();
            productResponse.setProductId(tmp.getProduct().getProductId());
            productResponse.setProductName(tmp.getProduct().getProductName());
            productResponse.setDescription(tmp.getProduct().getDescription());
            productResponse.setImage(tmp.getProduct().getImage());

            if(tmp.getPromotion() != null){
                Product productGift = productRepository.findById(tmp.getPromotion().getGift().getProductId())
                        .orElseThrow(null);
                GiftResponse giftResponse = new GiftResponse();
                giftResponse.setProductId(productGift.getProductId());
                giftResponse.setProductName(productGift.getProductName());
                giftResponse.setDescription(productGift.getDescription());
                giftResponse.setImage(productGift.getImage());
                giftResponse.setQuantityOfGift(tmp.getPromotion().getQuantityOfGift());
                productResponse.setGiftResponse(giftResponse);
            }else {
                productResponse.setGiftResponse(null);
            }
            orderDetailDto.setProduct(productResponse);
            orderDetailDTOs.add(orderDetailDto);
        }
        return orderDetailDTOs;
    }

    public OrderCancelResponse mapOrderToOrderResponse(OrderCancel orderCancel){
        List<OrderDetailDto> orderDetailDTOs = mapToOrderDetailDto(orderCancel);

        OrderCancelResponse orderCancelResponse = new OrderCancelResponse();
        orderCancelResponse.setListOrderDetail(orderDetailDTOs);
        orderCancelResponse.setOrderDto(mapToOrderDto(orderCancel.getOrder()));
        orderCancelResponse.setOrderCancelDto(mapToOrderCancelDto(orderCancel));
        orderCancelResponse.setVoucherDto(orderCancel.getOrder().getVoucher() == null ? null : mapper.map(orderCancel.getOrder().getVoucher(), VoucherDto.class));
        return orderCancelResponse;
    }
}
