package com.motherlove.services;

import com.motherlove.models.payload.requestModel.OrderCancelReq;
import com.motherlove.models.payload.responseModel.OrderCancelResponse;
import org.springframework.data.domain.Page;

public interface IOrderCancelService {
    OrderCancelResponse saveOrderCancel(OrderCancelReq orderCancelReq);
    Page<OrderCancelResponse> getAllOrderCancel(int pageNo, int pageSize, String sortBy, String sortDir);
    Page<OrderCancelResponse> getAllOrderCancelOfMember(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    OrderCancelResponse getOrderCancel(Long cancelId);

}
