package com.motherlove.services.impl;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.OrderDetail;
import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.models.payload.responseModel.OrderResponse;
import com.motherlove.repositories.OrderDetailRepository;
import com.motherlove.repositories.OrderRepository;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
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
    public OrderResponse createOrder(List<CartItem> cartItems, Long userId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalAmount = 0;

        //Find User
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User")
        ));

        //Create Order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        user.ifPresent(order::setUser);
        order.setCreatedDate(LocalDateTime.now());
        order.setLastModifiedDate(LocalDateTime.now());


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
                orderDetail.setCreatedDate(LocalDateTime.now());
                orderDetail.setLastModifiedDate(LocalDateTime.now());
                totalAmount = totalAmount + (product.get().getPrice() * item.getQuantity());
            }
            orderDetails.add(orderDetail);
        }
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        Order orderSave = orderRepository.findByOrderDate(order.getOrderDate());
        return mapOrderToOrderResponse(orderSave);
    }

    private OrderDto mapToOrderDto(Order order){
        return mapper.map(order, OrderDto.class);
    }

    private List<OrderDetailDto> mapToOrderDetailDto(Order order){
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());
        return orderDetails.stream().map(
                orderDetail -> mapper.map(orderDetail, OrderDetailDto.class)
        ).toList();
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
        return orderResponse;
    }
}
