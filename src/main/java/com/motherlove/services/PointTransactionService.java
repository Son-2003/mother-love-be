package com.motherlove.services;

import com.motherlove.models.entities.PointTransaction;
import org.springframework.data.domain.Page;

public interface PointTransactionService {
    Page<PointTransaction> getAllTransactionsByUserId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);

    Page<PointTransaction> getAllTransactions(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
}
