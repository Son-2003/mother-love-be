package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.GiftResponse;
import com.motherlove.models.payload.responseModel.OrderResponse;
import com.motherlove.models.payload.responseModel.ProductOrderDetailResponse;
import com.motherlove.repositories.*;
import com.motherlove.services.IOrderDetailService;
import com.motherlove.services.IOrderService;
import com.motherlove.services.IVoucherService;
import com.motherlove.utils.GenericSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final IOrderDetailService orderDetailService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final IVoucherService voucherService;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper mapper;

    @Override
    public Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> ordersPage = orderRepository.findAll(pageable);

        return ordersPage.map(this::mapOrderToOrderResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> ordersByUserId = orderRepository.findByUser_UserId(userId, pageable);

        return ordersByUserId.map(this::mapOrderToOrderResponse);
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order"));

        return mapOrderToOrderResponse(order);
    }

    @Override
    public OrderResponse createOrder(List<CartItem> cartItems, Long userId, Long addressId, Long voucherId, boolean isPreOrder) {
        //Find User
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User")
        ));

        //Find Address of User
        Optional<Address> address = Optional.ofNullable(addressRepository.findByUser_UserIdAndAddressId(userId, addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address")
        ));

        //Create Order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        if (isPreOrder) {
            order.setStatus(OrderStatus.PRE_ORDER);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }
        order.setFeedBack(false);
        address.ifPresent(order::setAddress);
        user.ifPresent(order::setUser);

        //Create OrderDetail
        List<OrderDetail> orderDetails = orderDetailService.createOrderDetails(cartItems, order, isPreOrder);

        float totalAmount = orderDetails.stream().map(OrderDetail::getTotalPrice).reduce(0f, Float::sum);
        order.setTotalAmount(totalAmount);
        //Handle Voucher
        voucherService.handleVoucherInOrder(voucherId, userId, order);
        order.setAfterTotalAmount(order.getVoucher() != null ? totalAmount - order.getVoucher().getDiscount() : totalAmount);

        // Save Order and OrderDetails
        Order orderCreated = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        return mapOrderToOrderResponse(orderCreated);
    }

    @Override
    public Page<OrderResponse> searchOrder(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Order> ordersPage;
        Specification<Order> specification = specification(searchParams);

        if(userId != null){
            ordersPage = orderRepository.findAll(specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("userId"), userId)), pageable);
        }else ordersPage = orderRepository.findAll(specification, pageable);

        return ordersPage.map(this::mapOrderToOrderResponse);
    }

    //map các dkien của user thành 1 specification
    private Specification<Order> specification(Map<String, Object> searchParams){
        List<Specification<Order>> specs = new ArrayList<>();

        // Lặp qua từng entry trong searchParams để tạo các Specification tương ứng
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "status":
                    specs.add(GenericSpecification.fieldIn(key, (Collection<?>) value));
                    break;
                case "orderDateFrom":
                    // Nếu có cả orderDateFrom và orderDateTo, sử dụng fieldBetween để tạo Specification
                    if (searchParams.containsKey("orderDateTo")) {
                        specs.add(GenericSpecification.fieldBetween("orderDate", (LocalDateTime) searchParams.get("orderDateFrom"), (LocalDateTime) searchParams.get("orderDateTo")));
                    } else {
                        // Ngược lại, sử dụng fieldGreaterThan để tạo Specification
                        specs.add(GenericSpecification.fieldGreaterThan("orderDate", (LocalDateTime) value));
                    }
                    break;
                case "orderDateTo":
                    if (!searchParams.containsKey("orderDateFrom")) {
                        specs.add(GenericSpecification.fieldLessThan("orderDate", (LocalDateTime) value));
                    }
                    break;
                case "fullName":
                case "userName":
                    specs.add(GenericSpecification.joinFieldContains("user", key, (String) value));
                    break;
            }
        });

        // Tổng hợp tất cả các Specification thành một Specification duy nhất bằng cách sử dụng phương thức reduce của Stream
        //reduce de ket hop cac spec1(dk1), spec2(dk2),.. thanh 1 specification chung va cac spec ket hop voi nhau thono qua AND
        return specs.stream().reduce(Specification.where(null), Specification::and);
    }
    private OrderDto mapToOrderDto(Order order){
        return mapper.map(order, OrderDto.class);
    }

    private List<OrderDetailDto> mapToOrderDetailDto(Order order){
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());
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

    public OrderResponse mapOrderToOrderResponse(Order order){
        List<OrderDetailDto> orderDetailDTOs = mapToOrderDetailDto(order);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setListOrderDetail(orderDetailDTOs);
        orderResponse.setOrderDto(mapToOrderDto(order));
        orderResponse.setVoucherDto(order.getVoucher() == null ? null : mapper.map(order.getVoucher(), VoucherDto.class));
        return orderResponse;
    }
}
