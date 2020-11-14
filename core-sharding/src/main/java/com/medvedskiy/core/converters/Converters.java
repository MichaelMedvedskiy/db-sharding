package com.medvedskiy.core.converters;

import com.medvedskiy.core.models.Payment;
import com.medvedskiy.repository.dao.payment.PaymentEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for conversion of model to its Persistence representation
 */
public class Converters {

    /**
     * Converts Iterable of Payment to List of PaymentEntity
     *
     * @param payments
     * @return converted list
     */
    public static List<PaymentEntity> paymentDAOWrapper(Iterable<Payment> payments) {
        return StreamSupport.stream(payments.spliterator(), false).map(
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
