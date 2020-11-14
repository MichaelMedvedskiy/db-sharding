package com.medvedskiy.core.services;

import com.medvedskiy.repository.dao.association.AssociationEntity;
import com.medvedskiy.repository.dao.payment.PaymentEntity;
import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.PaymentEntityRepository;
import com.medvedskiy.repository.tenanting.ThreadLocalStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for calculating total sum of payments by Sender Id
 *
 * @see com.medvedskiy.core.models.Payment
 */
@Slf4j
@Service
public class TotalSumService {

    private final AssociationEntityRepository associationEntityRepository;

    private final PaymentEntityRepository paymentEntityRepository;

    public TotalSumService(
            AssociationEntityRepository associationEntityRepository,

            PaymentEntityRepository paymentEntityRepository
    ) {
        this.associationEntityRepository = associationEntityRepository;
        this.paymentEntityRepository = paymentEntityRepository;

    }

    /**
     * @param senderId id of sender to get sum for
     * @return total sum
     */
    public Long calculateTotalSumBySender(
            Long senderId
    ) {
        log.info("Getting total sum for SenderId: {}", senderId);
        AssociationEntity association = associationEntityRepository.findById(senderId).orElse(null);
        //if no record exists return 0
        if (association == null) {
            return 0L;
        }
        //get what db houses this sender's payments
        int dbIndex = association.getDbId();
        List<PaymentEntity> paymentsBySender = Collections.emptyList();
        ThreadLocalStorage.setTenantName(String.valueOf(dbIndex));
        paymentsBySender = paymentEntityRepository.findPaymentsBySender(senderId);
        return paymentsBySender.stream().map(PaymentEntity::getPrice).reduce(0L, Long::sum);

    }
}
