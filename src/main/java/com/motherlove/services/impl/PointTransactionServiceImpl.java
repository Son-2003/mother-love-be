package com.motherlove.services.impl;

import com.motherlove.models.entities.PointTransaction;
import com.motherlove.repositories.PointTransactionRepository;
import com.motherlove.services.IPointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointTransactionServiceImpl implements IPointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;


    @Override
    public Page<PointTransaction> getAllTransactionsByUserId(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return pointTransactionRepository.findAllByUser_UserId(userId, pageable);
    }

    @Override
    public Page<PointTransaction> getAllTransactions(int pageNo, int pageSize, String sortBy, String sortDir, Long userId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return pointTransactionRepository.findAll(pageable);
    }
}
