package com.motherlove.services;

import com.motherlove.models.payload.dto.PaymentMethodDto;
import com.motherlove.models.payload.responseModel.PaymentMethodResponse;

public interface IPaymentMethodService {
    PaymentMethodDto addPaymentMethod(PaymentMethodDto categoryDto);
    PaymentMethodDto getPaymentMethod(Long id);
    PaymentMethodResponse getAllPaymentMethods(int pageNo, int pageSize, String sortBy, String sortDir);
    PaymentMethodDto updatePaymentMethod(PaymentMethodDto paymentMethodDto, long paymentMethodId);
    void deletePaymentMethod(long id);
}
