package com.motherlove.models.payload.responseModel;

import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.dto.PaymentHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponse {
    private List<PaymentHistoryDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
