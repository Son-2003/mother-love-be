package com.motherlove.services;


import com.motherlove.models.payload.dto.FeedbackDto;
import com.motherlove.models.payload.requestModel.FeedbackCreateReq;
import com.motherlove.models.payload.requestModel.FeedbackUpdateReq;
import com.motherlove.models.payload.responseModel.FeedbackResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IFeedbackService {
    List<FeedbackDto> createFeedback(List<FeedbackCreateReq> feedbackCreateReqs, Long userId, Long orderId);
    FeedbackResponse searchFeedback(Map<String, Object> searchParams, Long productId);
    Page<FeedbackResponse> viewFeedbackInManage(int pageNo, int pageSize, String sortBy, String sortDir);
    FeedbackResponse viewFeedback(Long productId);
    List<FeedbackDto> viewFeedbackOfOrder(Long orderId);
    FeedbackDto updateFeedback(FeedbackUpdateReq feedbackUpdateReq);
    void deleteFeedback(Long feedbackId);

}
