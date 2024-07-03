package com.motherlove.services.impl;

import com.motherlove.models.entities.Feedback;
import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.Product;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.FeedbackDto;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.models.payload.requestModel.FeedbackCreateReq;
import com.motherlove.models.payload.requestModel.FeedbackUpdateReq;
import com.motherlove.models.payload.responseModel.FeedbackDetail;
import com.motherlove.models.payload.responseModel.FeedbackResponse;
import com.motherlove.models.payload.responseModel.ProductResponse;
import com.motherlove.repositories.*;
import com.motherlove.services.IFeedbackService;
import com.motherlove.utils.GenericSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackServiceImpl implements IFeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public List<FeedbackDto> createFeedback(List<FeedbackCreateReq> feedbackCreateReqs, Long userId, Long orderId) {
        List<Feedback> feedbacks = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User")
        );
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order")
        );
        if(order.isFeedBack()){
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Order is already feedback!");
        }else order.setFeedBack(true);
        orderRepository.save(order);

        for(FeedbackCreateReq feedback: feedbackCreateReqs){
            Product product = productRepository.findById(feedback.getProductId()).orElseThrow(
                    () -> new ResourceNotFoundException("Product")
            );

            Feedback feedbackCreate = new Feedback();
            feedbackCreate.setRating(feedback.getRating());
            feedbackCreate.setComment(feedback.getComment());
            feedbackCreate.setImage(feedback.getImage());
            feedbackCreate.setFeedbackDate(LocalDateTime.now());
            feedbackCreate.setProduct(product);
            feedbackCreate.setUser(user);
            feedbackCreate.setOrder(order);

            feedbacks.add(feedbackCreate);
        }
        feedbacks = feedbackRepository.saveAll(feedbacks);
        return feedbacks.stream().map(this::mapToDto).toList();
    }

    @Override
    public FeedbackResponse searchFeedback(Map<String, Object> searchParams, Long productId) {
        Specification<Feedback> specification = specification(searchParams);
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        Optional<Product> product = productRepository.findById(productId);

        feedbackResponse.setProduct(mapper.map(product, ProductResponse.class));
        List<FeedbackDetail> feedbackDetails = feedbackRepository.findAll(specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("product").get("productId"), productId))).stream().map(this::mapToDetail).toList();
        feedbackResponse.setFeedbackDetails(feedbackDetails);

        return feedbackResponse;
    }

    @Override
    public Page<FeedbackResponse> viewFeedbackInManage(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<Product> products = productRepository.findAll();
        List<FeedbackResponse> feedbackResponses = new ArrayList<>();
        for (Product product: products){
            FeedbackResponse feedbackResponse = new FeedbackResponse();
            feedbackResponse.setProduct(mapper.map(product, ProductResponse.class));

            List<FeedbackDetail> feedbackDetails = feedbackRepository.findByProduct_ProductId(product.getProductId())
                    .stream().map(this::mapToDetail).toList();

            feedbackResponse.setFeedbackDetails(feedbackDetails);
            feedbackResponses.add(feedbackResponse);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), feedbackResponses.size());
        return new PageImpl<>(feedbackResponses.subList(start, end), pageable, feedbackResponses.size());
    }

    @Override
    public FeedbackResponse viewFeedback(Long productId) {
        Optional<Product> product = productRepository.findById(productId);

        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setProduct(mapper.map(product, ProductResponse.class));
        List<FeedbackDetail> feedbackDetails = feedbackRepository.findByProduct_ProductId(productId)
                .stream().map(this::mapToDetail).toList();
        feedbackResponse.setFeedbackDetails(feedbackDetails);

        return feedbackResponse;
    }

    @Override
    public List<FeedbackDto> viewFeedbackOfOrder(Long orderId) {
        List<Feedback> feedbacks = feedbackRepository.findByOrder_OrderId(orderId);
        return feedbacks.stream().map(this::mapToDto).toList();
    }

    @Override
    public FeedbackDto updateFeedback(FeedbackUpdateReq feedbackUpdateReq) {
        Feedback feedback = feedbackRepository.findById(feedbackUpdateReq.getFeedbackId())
                .orElseThrow(() -> new ResourceNotFoundException("Feedback"));
            feedback.setRating(feedbackUpdateReq.getRating());
            feedback.setComment(feedbackUpdateReq.getComment());
            feedback.setImage(feedbackUpdateReq.getImage());
        return mapToDto(feedbackRepository.save(feedback));
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Optional<Feedback> feedback = Optional.ofNullable(feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback")));
        feedback.ifPresent(feedbackRepository::delete);
    }

    private FeedbackDetail mapToDetail(Feedback feedback){
        FeedbackDetail feedbackDetail = new FeedbackDetail();
        feedbackDetail.setFeedbackId(feedback.getFeedbackId());
        feedbackDetail.setRating(feedback.getRating());
        feedbackDetail.setComment(feedback.getComment());
        feedbackDetail.setImage(feedback.getImage());
        feedbackDetail.setFeedbackDate(feedback.getFeedbackDate());
        feedbackDetail.setUser(mapper.map(feedback.getUser(), UserDto.class));
        return feedbackDetail;
    }

    private FeedbackDto mapToDto(Feedback feedback){
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setFeedbackId(feedback.getFeedbackId());
        feedbackDto.setRating(feedback.getRating());
        feedbackDto.setComment(feedback.getComment());
        feedbackDto.setImage(feedback.getImage());
        feedbackDto.setFeedbackDate(feedback.getFeedbackDate());
        feedbackDto.setProduct(mapper.map(feedback.getProduct(), ProductResponse.class));
        feedbackDto.setUser(mapper.map(feedback.getUser(), UserDto.class));

        return feedbackDto;
    }

    private Specification<Feedback> specification(Map<String, Object> searchParams){
        List<Specification<Feedback>> specs = new ArrayList<>();

        // Lặp qua từng entry trong searchParams để tạo các Specification tương ứng
        searchParams.forEach((key, value) -> {
            switch (key) {
                case "rating" -> specs.add(GenericSpecification.fieldIn(key, (Collection<?>) value));
                case "productName" -> specs.add(GenericSpecification.joinFieldContains("product", key, (String) value));
                case "brandName" -> specs.add(GenericSpecification.joinFieldIn("brand", key, (Collection<?>) value));
                case "categoryName" ->
                        specs.add(GenericSpecification.joinFieldIn("category", key, (Collection<?>) value));
            }
        });

        // Tổng hợp tất cả các Specification thành một Specification duy nhất bằng cách sử dụng phương thức reduce của Stream
        //reduce de ket hop cac spec1(dk1), spec2(dk2),.. thanh 1 specification chung va cac spec ket hop voi nhau thono qua AND
        return specs.stream().reduce(Specification.where(null), Specification::and);
    }
}
