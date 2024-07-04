package com.motherlove.services.impl;

import com.motherlove.models.entities.*;
import com.motherlove.models.enums.OrderStatus;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.StockTransactionDto;
import com.motherlove.models.payload.requestModel.ImportProduct;
import com.motherlove.repositories.*;
import com.motherlove.services.IStockTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockTransactionServiceImpl implements IStockTransactionService {
    private final StockTransactionRepository stockTransactionRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper mapper;

    @Override
    public StockTransactionDto importProduct(ImportProduct importProduct) {
        Supplier supplier = supplierRepository.findById(importProduct.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier"));
        Product product = productRepository.findById(importProduct.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product"));
        //Update Quantity Product
        product.setQuantityProduct(product.getQuantityProduct() + importProduct.getQuantity());

        //Update Order have status "PRE_ODER"
        List<Order> orders = orderRepository.findByStatusOrderByOrderDate(OrderStatus.PRE_ORDER);

        for(Order order: orders){
            boolean hasZeroQuantity = order.getOrderDetails().stream()
                    .anyMatch(orderDetail -> orderDetail.getProduct().getQuantityProduct() == 0);
            if(!hasZeroQuantity){
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    product.setQuantityProduct(product.getQuantityProduct() - orderDetail.getQuantity());
                    productRepository.save(product);
                    orderDetailRepository.save(orderDetail);
                }
                order.setStatus(OrderStatus.PENDING);
            }
        }
        StockTransaction stockTransaction = new StockTransaction();
        stockTransaction.setStockTransactionDate(LocalDateTime.now());
        stockTransaction.setQuantity(importProduct.getQuantity());
        stockTransaction.setTotalPrice(importProduct.getQuantity() * product.getPrice());
        stockTransaction.setProduct(product);
        stockTransaction.setSupplier(supplier);

        return mapToStockTransactionDto(stockTransactionRepository.save(stockTransaction));
    }

    @Override
    public Page<StockTransactionDto> getAllStockTransaction(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<StockTransaction> stockTransactions = stockTransactionRepository.findAll(pageable);

        return stockTransactions.map(this::mapToStockTransactionDto);
    }

    @Override
    public StockTransactionDto getStockTransactionById(Long stockTransactionId) {
        StockTransaction stockTransaction = stockTransactionRepository.findById(stockTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Transaction"));

        return mapToStockTransactionDto(stockTransaction);
    }

    private StockTransactionDto mapToStockTransactionDto(StockTransaction stockTransaction){
        return mapper.map(stockTransaction, StockTransactionDto.class);
    }

}
