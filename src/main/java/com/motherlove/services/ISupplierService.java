package com.motherlove.services;

import com.motherlove.models.payload.dto.SupplierDto;
import com.motherlove.models.payload.responseModel.SupplierResponse;

public interface ISupplierService {
    SupplierDto addSupplier(SupplierDto supplierDto);
    SupplierDto getSupplier(Long id);
    SupplierResponse getAllSuppliers(int pageNo, int pageSize, String sortBy, String sortDir);
    SupplierDto updateSupplier(Long id , SupplierDto supplierDto);
    void deleteSupplier(long id);
}
