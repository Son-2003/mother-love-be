package com.motherlove.services.impl;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.OrderDetail;
import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.Promotion;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.repositories.PromotionRepository;
import com.motherlove.services.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements IOrderDetailService {

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    @Override
    public List<OrderDetail> createOrderDetails(List<CartItem> cartItems, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();

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

            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }
}
