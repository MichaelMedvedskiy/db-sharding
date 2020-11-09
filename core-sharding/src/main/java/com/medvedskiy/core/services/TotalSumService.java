package com.medvedskiy.core.services;

import com.medvedskiy.repository.dao.AssociationEntity;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.db1.PaymentEntityDB1Repository;
import com.medvedskiy.repository.repositories.payment.db2.PaymentEntityDB2Repository;
import com.medvedskiy.repository.repositories.payment.db3.PaymentEntityDB3Repository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for calculating total sum of payments by Sender Id
 *
 * @see com.medvedskiy.core.models.Payment
 */
@Service
public class TotalSumService {

    private final AssociationEntityRepository associationEntityRepository;


    private final PaymentEntityDB1Repository firstDatabasePaymentRepository;

    private final PaymentEntityDB2Repository secondDatabasePaymentRepository;

    private final PaymentEntityDB3Repository thirdDatabasePaymentRepository;


    public TotalSumService(
            AssociationEntityRepository associationEntityRepository,

            PaymentEntityDB1Repository firstDatabasePaymentRepository,

            PaymentEntityDB2Repository secondDatabasePaymentRepository,

            PaymentEntityDB3Repository thirdDatabasePaymentRepository
    ) {
        this.associationEntityRepository = associationEntityRepository;
        this.firstDatabasePaymentRepository = firstDatabasePaymentRepository;
        this.secondDatabasePaymentRepository = secondDatabasePaymentRepository;
        this.thirdDatabasePaymentRepository = thirdDatabasePaymentRepository;

    }

    /**
     * @param senderId id of sender to get sum for
     * @return total sum
     */
    public Long calculateTotalSumBySender(
            Long senderId
    ) {
        AssociationEntity association = associationEntityRepository.findOne(senderId);
        //if no record exists return 0
        if (association == null) {
            return 0L;
        }
        //get what db houses this sender's payments
        int dbIndex = association.getDbId();
        List<PaymentEntity> paymentsBySender = Collections.emptyList();
        //get sum
        if (dbIndex == 0) {
            paymentsBySender = firstDatabasePaymentRepository.findPaymentsBySender(senderId);
        } else if(dbIndex == 1) {
            paymentsBySender = secondDatabasePaymentRepository.findPaymentsBySender(senderId);
        } else if(dbIndex == 2) {
            paymentsBySender = thirdDatabasePaymentRepository.findPaymentsBySender(senderId);
        }
        return paymentsBySender.stream().map(PaymentEntity::getPrice).reduce(0L, Long::sum);
    }
}
