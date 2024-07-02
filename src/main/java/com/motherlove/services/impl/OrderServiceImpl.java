package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

        List<Order> ordersPage = orderRepository.findAll();

        List<OrderResponse> orderResponses = mapListOrderToOrderResponse(ordersPage);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderResponses.size());
        return new PageImpl<>(orderResponses.subList(start, end), pageable, orderResponses.size());
    }

    @Override
    public Page<OrderResponse> getAllOrderByCustomerId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<Order> ordersByUserId = orderRepository.findByUser_UserId(userId);

        List<OrderResponse> orderResponses = mapListOrderToOrderResponse(ordersByUserId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderResponses.size());
        return new PageImpl<>(orderResponses.subList(start, end), pageable, orderResponses.size());
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order"));

        return mapOrderToOrderResponse(order);
    }

    @Override
    public OrderResponse createOrder(List<CartItem> cartItems, Long userId, Long addressId, Long voucherId) {
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
        order.setStatus(1);
        order.setFeedBack(false);
        address.ifPresent(order::setAddress);
        user.ifPresent(order::setUser);

        //Create OrderDetail
        List<OrderDetail> orderDetails = orderDetailService.createOrderDetails(cartItems, order);

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
    public Page<OrderResponse> searchOrder(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Order> specification = specification(searchParams);

        // Query database with Specification and userId
        List<Order> ordersPage = orderRepository.findAll(specification);

        List<OrderResponse> orderResponses = mapListOrderToOrderResponse(ordersPage);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderResponses.size());
        return new PageImpl<>(orderResponses.subList(start, end), pageable, orderResponses.size());
    }

    @Override
    public Page<OrderResponse> searchOrderUser(int pageNo, int pageSize, String sortBy, String sortDir, Map<String, Object> searchParams, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Order> specification = specification(searchParams);

        // Query database with Specification and userId
        List<Order> ordersPage = orderRepository.findAll(specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("userId"), userId)));

        List<OrderResponse> orderResponses = mapListOrderToOrderResponse(ordersPage);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderResponses.size());
        return new PageImpl<>(orderResponses.subList(start, end), pageable, orderResponses.size());
    }

    private Specification<Order> specification(Map<String, Object> searchParams){
        List<Specification<Order>> specs = new ArrayList<>();

        // Lặp qua từng entry trong searchParams để tạo các Specification tương ứng
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "status":
                case "isFeedBack":
                    specs.add(GenericSpecification.fieldEquals(key, value));
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
                case "minAmount":
                    if (searchParams.containsKey("maxAmount")) {
                        specs.add(GenericSpecification.fieldBetween("afterTotalAmount", (Float) searchParams.get("minAmount"), (Float) searchParams.get("maxAmount")));
                    } else {
                        specs.add(GenericSpecification.fieldGreaterThan("afterTotalAmount", (Float) value));
                    }
                    break;
                case "maxAmount":
                    if (!searchParams.containsKey("minAmount")) {
                        specs.add(GenericSpecification.fieldLessThan("afterTotalAmount", (Float) value));
                    }
                    break;
                case "methodName":
                case "voucherCode":
                case "fullName":
                case "phone":
                    specs.add(GenericSpecification.fieldContains(key, (String) value));
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

    public List<OrderResponse> mapListOrderToOrderResponse(List<Order> orders){
        List<OrderResponse> orderResponses = new ArrayList<>();

        for(Order order : orders){
            List<OrderDetailDto> orderDetailDTOs = mapToOrderDetailDto(order);
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setListOrderDetail(orderDetailDTOs);
            orderResponse.setOrderDto(mapToOrderDto(order));
            orderResponses.add(orderResponse);
        }
        return orderResponses;
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
