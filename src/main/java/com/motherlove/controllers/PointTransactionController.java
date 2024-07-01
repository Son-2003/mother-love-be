package com.motherlove.controllers;

import com.motherlove.models.entities.PointTransaction;
import com.motherlove.models.payload.dto.PointTransactionDto;
import com.motherlove.services.IPointTransactionService;
import com.motherlove.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/point-transaction")
@RequiredArgsConstructor
public class PointTransactionController {

    private final IPointTransactionService pointTransactionService;
    private final ModelMapper mapper;


    public PointTransactionDto mapEntityToDto(PointTransaction entity) {
        return mapper.map(entity, PointTransactionDto.class);
    }


    @GetMapping("/get-by-user")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    //TODO: authorize user has permission with transation
    public ResponseEntity<Object> getAllTransactionByUserId(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "transactionId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "userId") Long userId
    ) {
        var list = pointTransactionService.getAllTransactionsByUserId(pageNo, pageSize, sortBy, sortDir, userId);
        List<PointTransactionDto> result = list.stream().map(this::mapEntityToDto).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<Object> getAllTransaction(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "transactionId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "userId") Long userId
    ) {
        var list = pointTransactionService.getAllTransactions(pageNo, pageSize, sortBy, sortDir, userId);
        List<PointTransactionDto> result = list.stream().map(this::mapEntityToDto).toList();
        return ResponseEntity.ok(result);
    }


}
