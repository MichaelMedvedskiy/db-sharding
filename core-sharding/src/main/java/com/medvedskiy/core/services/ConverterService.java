package com.medvedskiy.core.services;

import com.medvedskiy.repository.dao.PaymentEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConverterService {

    public List<PaymentEntity> paymentDAOWrapper(List<com.medvedskiy.core.models.Payment> payments) {
        return payments.stream().map(
                payment -> {
                    PaymentEntity paymentEntityDAO = new PaymentEntity();
                    paymentEntityDAO.setPrice(payment.price());
                    paymentEntityDAO.setReceiver(payment.receiver());
                    paymentEntityDAO.setSender(payment.sender());
                    return paymentEntityDAO;
                }
        ).collect(Collectors.toList());
    }

}
