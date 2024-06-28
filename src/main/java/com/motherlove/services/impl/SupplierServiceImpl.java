package com.motherlove.services.impl;

import com.motherlove.models.entities.Blog;
import com.motherlove.models.entities.Category;
import com.motherlove.models.entities.Supplier;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.CategoryDto;
import com.motherlove.models.payload.dto.SupplierDto;
import com.motherlove.models.payload.responseModel.SupplierResponse;
import com.motherlove.repositories.SupplierRepository;
import com.motherlove.services.SupplierService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {
     private final SupplierRepository supplierRepository;
     private final ModelMapper modelMapper;

     @Autowired
     public SupplierServiceImpl(SupplierRepository supplierRepository, ModelMapper modelMapper) {
         this.supplierRepository = supplierRepository;
         this.modelMapper = modelMapper;
     }

     @Override
     public SupplierDto addSupplier(SupplierDto supplierDto) {
         Supplier supplier = modelMapper.map(supplierDto, Supplier.class);
         Supplier checkSupplier = supplierRepository.findBySupplierName(supplier.getSupplierName());
            if (checkSupplier != null) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This supplier already exists");
            }
         Supplier savedSupplier = supplierRepository.save(supplier);
         return modelMapper.map(savedSupplier, SupplierDto.class);
     }

    @Override
     public SupplierDto getSupplier(Long id) {
         Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
         return modelMapper.map(supplier, SupplierDto.class);
     }

     @Override
     public SupplierResponse getAllSuppliers(int pageNo, int pageSize, String sortBy, String sortDir) {
         Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

         //create Pageable instance
         Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
         Page<Supplier> posts = supplierRepository.findAll(pageable);

         //get content of page
         List<Supplier> supplierList = posts.getContent();

         //format the response
         List<SupplierDto> content = supplierList.stream().map(supplier -> modelMapper.map(supplier, SupplierDto.class)).collect(Collectors.toList());
         SupplierResponse supplierResponse = new SupplierResponse();
         supplierResponse.setContent(content);
         supplierResponse.setPageNo(posts.getNumber());
         supplierResponse.setPageSize(posts.getSize());
         supplierResponse.setTotalElements(posts.getTotalElements());
         supplierResponse.setTotalPages(posts.getTotalPages());
         supplierResponse.setLast(posts.isLast());

         return supplierResponse;
     }

    @Override
    public Page<SupplierDto> searchSuppliers(int pageNo, int pageSize, String sortBy, String sortDir, String searchText) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Supplier> suppliers = supplierRepository.searchSuppliers(searchText, pageable);
        return suppliers.map(s -> modelMapper.map(s, SupplierDto.class));
    }

    @Override
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierDto.getSupplierId()));
        Supplier checkSupplier = supplierRepository.findBySupplierName(supplierDto.getSupplierName());
        if (checkSupplier != null) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "This supplier already exists");
        }
        supplier.setSupplierName(supplierDto.getSupplierName());
        supplier.setContactInfo(supplierDto.getContactInfo());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setPhone(supplierDto.getPhone());
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return modelMapper.map(updatedSupplier, SupplierDto.class);
    }

    @Override
    public void deleteSupplier(long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        if (!supplier.getStockTransactions().isEmpty()) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "There is at least one stock transaction belongs to this supplier");
        }
        supplierRepository.delete(supplier);
    }
}
