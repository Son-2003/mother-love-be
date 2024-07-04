package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
import com.motherlove.models.enums.ProductStatus;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.requestModel.CartItem;
import com.motherlove.repositories.ProductRepository;
import com.motherlove.repositories.PromotionRepository;
import com.motherlove.services.IEmailService;
import com.motherlove.services.IOrderDetailService;
import jakarta.mail.MessagingException;
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
    private final IEmailService emailService;
    @Override
    public List<OrderDetail> createOrderDetails(List<CartItem> cartItems, Order order, boolean isPreOrder, User user) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        //Create OrderDetail
        for (CartItem item : cartItems){
            OrderDetail orderDetail = new OrderDetail();
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product"));
            if((isPreOrder && product.getStatus() != ProductStatus.PRE_ORDER) || (!isPreOrder && product.getStatus() == ProductStatus.PRE_ORDER)){
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This product cannot PreOrder!");
            }
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(product.getPrice());
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setTotalPrice(product.getPrice() * item.getQuantity());

            if(!isPreOrder){
                //Update quantity of Product
                int quantityUpdated = product.getQuantityProduct();
                if(quantityUpdated > item.getQuantity()){
                    product.setQuantityProduct(product.getQuantityProduct() - item.getQuantity());
                    if(product.getQuantityProduct() < 15){
                        String content = "<html>" +
                                "<head>" +
                                "<style>" +
                                "table { width: 100%; border-collapse: collapse; }" +
                                "th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }" +
                                "th { background-color: #f2f2f2; }" +
                                "body { font-family: Arial, sans-serif; }" +
                                ".highlight { font-weight: bold; color: red; }" +
                                "</style>" +
                                "</head>" +
                                "<body>" +
                                "<p class='greeting'>Xin chào đối tác/nhà cung cấp,</p>" +
                                "<p>Chúng tôi xin thông báo rằng sản phẩm trong kho của chúng tôi đang cạn kiệt và cần nhập thêm hàng hóa từ bạn:</p>" +
                                "<table>" +
                                "<tr><th>Mã sản phẩm</th><td>" + product.getProductId() + "</td></tr>" +
                                "<tr><th>Tên sản phẩm</th><td>" + product.getProductName() + "</td></tr>" +
                                "<tr><th>Số lượng hiện tại trong kho</th><td>" + product.getQuantityProduct() + "</td></tr>" +
                                "<tr><th>Số lượng cần nhập thêm</th><td class='highlight'>" + 50 + "</td></tr>" +
                                "</table>" +
                                "<p>Xin vui lòng cung cấp thông tin về quy trình đặt hàng và dự kiến thời gian giao hàng.</p>" +
                                "<p>Xin chân thành cảm ơn sự hỗ trợ của bạn.</p>" +
                                "</body>" +
                                "</html>";

                        try {
                            emailService.sendEmail("nguyenducson2915@gmail.com", "[MotherLove] - Yêu cầu nhập hàng mới", content);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }
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
            }

            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }
}
