package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.OrderDetailDto;
import com.motherlove.models.payload.dto.OrderDto;
import com.motherlove.models.payload.dto.VoucherDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private OrderDto orderDto;
    private VoucherDto voucherDto;
    private List<OrderDetailDto> listOrderDetail;
}
