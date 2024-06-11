package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.PaymentMethodDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodResponse {
    private List<PaymentMethodDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
