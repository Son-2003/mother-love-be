package com.motherlove.services;

import com.motherlove.models.payload.dto.StockTransactionDto;
import com.motherlove.models.payload.requestModel.ImportProduct;
import org.springframework.data.domain.Page;

public interface IStockTransactionService {
    StockTransactionDto importProduct(ImportProduct importProduct);
    Page<StockTransactionDto> getAllStockTransaction(int pageNo, int pageSize, String sortBy, String sortDir);
    StockTransactionDto getStockTransactionById(Long stockTransactionId);
}
