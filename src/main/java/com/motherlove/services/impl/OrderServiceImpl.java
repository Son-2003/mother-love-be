package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.GiftResponse;
import com.motherlove.models.payload.responseModel.OrderResponse;
import com.motherlove.models.payload.responseModel.ProductOrderDetailResponse;
import com.motherlove.repositories.*;
import com.motherlove.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final PromotionRepository promotionRepository;
    private final ModelMapper mapper;

    @Override
    public Page<OrderResponse> getAllOrder(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<Order> orders = orderRepository.findAll();

        List<OrderResponse> orderResponses = mapListOrderToOrderResponse(orders);

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
    public OrderResponse createOrder(List<CartItem> cartItems, Long userId, Long addressId, Long voucherId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalAmount = 0;

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

        //Find Voucher, Save Voucher in OrderVoucher, Discount totalPrice(if have voucher), Update use voucher in CustomerVoucher
        if(voucherId != 0){
            Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(
                    () -> new ResourceNotFoundException("Voucher")
            );
            CustomerVoucher customerVoucher = customerVoucherRepository.findByVoucher_VoucherIdAndUser_UserId(voucherId, userId);

            if(voucher.getStartDate().isAfter(LocalDateTime.now()) || voucher.getEndDate().isBefore(LocalDateTime.now()))
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Voucher of user is not valid!");
            else if(customerVoucher.isUsed()){
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This voucher is already used");
            }
            customerVoucher.setUsed(true);
            customerVoucher.setUsedDate(LocalDateTime.now());
            order.setVoucher(voucher);
            order.setTotalAmount(totalAmount);
            order.setAfterTotalAmount(totalAmount - voucher.getDiscount());
        }else{
            order.setVoucher(null);
            order.setTotalAmount(totalAmount);
            order.setAfterTotalAmount(totalAmount);
        }

        //Create OrderDetail
        for (CartItem item : cartItems){
            OrderDetail orderDetail = new OrderDetail();
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product"));

                orderDetail.setQuantity(item.getQuantity());
                orderDetail.setUnitPrice(product.getPrice());
                orderDetail.setOrder(order);
                orderDetail.setProduct(product);
                orderDetail.setTotalPrice(product.getPrice() * item.getQuantity());

                //Update quantity of Product
                int quantityUpdated = product.getQuantityProduct();
                if(quantityUpdated > item.getQuantity()){
                    product.setQuantityProduct(product.getQuantityProduct() - item.getQuantity());
                }else {
                    throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Quantity of " + product.getProductName() + " is not enough!");
                }

                //Get Promotion of Product and update quantity gift in Promotion
                Optional<Promotion> promotion = promotionRepository.findPromotionValid(product.getProductId());
                if(promotion.isPresent()){
                    if(promotion.get().getAvailableQuantity() < promotion.get().getQuantityOfGift()){
                        throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Quantity of " + product.getProductName() + "'s gift is not enough!");
                    }else {
                        promotion.get().setAvailableQuantity(promotion.get().getAvailableQuantity() - promotion.get().getQuantityOfGift());
                        orderDetail.setPromotion(promotion.get());
                    }
                }else orderDetail.setPromotion(null);

                //Sum TotalPrice in Order
                totalAmount = totalAmount + (product.getPrice() * item.getQuantity());

            orderDetails.add(orderDetail);
        }

        Order orderCreated = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return mapOrderToOrderResponse(orderCreated);
    }

    private OrderDto mapToOrderDto(Order order){
        return mapper.map(order, OrderDto.class);
    }

    private List<OrderDetailDto> mapToOrderDetailDto(Order order){
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());
        List<OrderDetailDto> orderDetailDTOs = new ArrayList<>();
        for (OrderDetail tmp: orderDetails){
            Product product = productRepository.findById(tmp.getProduct().getProductId())
                    .orElseThrow(null);
            OrderDetailDto orderDetailDto = mapper.map(tmp, OrderDetailDto.class);
            ProductOrderDetailResponse productResponse = new ProductOrderDetailResponse();
            productResponse.setProductId(product.getProductId());
            productResponse.setProductName(product.getProductName());
            productResponse.setDescription(product.getDescription());
            productResponse.setImage(product.getImage());

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
