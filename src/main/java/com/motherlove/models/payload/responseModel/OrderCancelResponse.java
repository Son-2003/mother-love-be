package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.OrderCancelDto;
import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.VoucherDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelResponse {
    private OrderCancelDto orderCancelDto;
    private OrderDto orderDto;
    private VoucherDto voucherDto;
    private List<OrderDetailDto> listOrderDetail;
}
