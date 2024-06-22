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

        //Create OrderDetail
        for (CartItem item : cartItems){
            OrderDetail orderDetail = new OrderDetail();
            Optional<Product> product = Optional.ofNullable(productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product")));

            if(product.isPresent()){
                orderDetail.setQuantity(item.getQuantity());
                orderDetail.setUnitPrice(product.get().getPrice());
                orderDetail.setOrder(order);
                orderDetail.setProduct(product.get());
                orderDetail.setTotalPrice(product.get().getPrice() * item.getQuantity());

                //Update quantity of Product
                int quantityUpdated = product.get().getQuantityProduct();
                if(quantityUpdated > item.getQuantity()){
                    product.get().setQuantityProduct(product.get().getQuantityProduct() - item.getQuantity());
                }else {
                    throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Quantity of " + product.get().getProductName() + " is not enough!");
                }

                //Get Promotion of Product and Update available Quantity in Promotion
                Optional<Promotion> promotion = promotionRepository.findTopByProductIdOrderByCreatedDateDesc(product.get().getProductId());
                if(promotion.isPresent()){
                    promotion.get().setAvailableQuantity(promotion.get().getAvailableQuantity() - promotion.get().getQuantityOfGift());
                    promotionRepository.save(promotion.get());
                    orderDetail.setPromotion(promotion.get());
                }else orderDetail.setPromotion(null);

                //Sum TotalPrice in Order
                totalAmount = totalAmount + (product.get().getPrice() * item.getQuantity());
            }
            orderDetails.add(orderDetail);
        }

        //Find Voucher and Save Voucher in OrderVoucher and Discount totalPrice(if have voucher)
        if(voucherId != 0){
            Optional<Voucher> voucher = Optional.ofNullable(voucherRepository.findById(voucherId).orElseThrow(
                    () -> new ResourceNotFoundException("Voucher")
            ));
            if(voucher.get().getStartDate().isAfter(LocalDateTime.now()) || voucher.get().getEndDate().isBefore(LocalDateTime.now()))
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Voucher is not valid!");
            voucher.ifPresent(order::setVoucher);
            order.setTotalAmount(totalAmount);
            order.setAfterTotalAmount(totalAmount - voucher.get().getDiscount());
        }else{
            order.setVoucher(null);
            order.setTotalAmount(totalAmount);
            order.setAfterTotalAmount(totalAmount);
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
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for (OrderDetail tmp: orderDetails){
            Optional<Product> product = productRepository.findById(tmp.getProduct().getProductId());
            OrderDetailDto orderDetailDto = mapper.map(tmp, OrderDetailDto.class);
            ProductOrderDetailResponse productResponse = new ProductOrderDetailResponse();
            productResponse.setProductId(product.get().getProductId());
            productResponse.setProductName(product.get().getProductName());
            productResponse.setDescription(product.get().getDescription());
            productResponse.setImage(product.get().getImage());
            if(tmp.getPromotion() != null){
                Optional<Product> productGift = productRepository.findById(tmp.getPromotion().getGift().getProductId());
                GiftResponse giftResponse = new GiftResponse();
                giftResponse.setProductId(productGift.get().getProductId());
                giftResponse.setProductName(productGift.get().getProductName());
                giftResponse.setDescription(productGift.get().getDescription());
                giftResponse.setImage(productGift.get().getImage());
                giftResponse.setQuantityOfGift(tmp.getPromotion().getQuantityOfGift());
                productResponse.setGiftResponse(giftResponse);
            }else {
                productResponse.setGiftResponse(null);
            }
            orderDetailDto.setProduct(productResponse);
            orderDetailDtos.add(orderDetailDto);
        }
        return orderDetailDtos;
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
