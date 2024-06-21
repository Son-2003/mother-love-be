package com.motherlove.services.impl;

import com.motherlove.models.entities.Order;
import com.motherlove.models.entities.PointTransaction;
import com.motherlove.models.entities.User;
import com.motherlove.repositories.OrderRepository;
import com.motherlove.repositories.PointTransactionRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.PointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointTransactionServiceImpl implements PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;


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

    @Override
    public PointTransaction savePointForUser(Long userId, Long orderId) {
        User user = userRepository.findById(userId).orElseThrow();
        Order order = orderRepository.findById(orderId).orElseThrow();

        PointTransaction pointTransaction = PointTransaction.builder()
                .transactionDate(LocalDateTime.now())
                .user(user)
                .points((long)(order.getTotalAmount() / 1000))
                .build();
        return pointTransactionRepository.save(pointTransaction);
    }
}
