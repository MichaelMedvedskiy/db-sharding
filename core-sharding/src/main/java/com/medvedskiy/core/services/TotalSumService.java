package com.medvedskiy.core.services;

import com.medvedskiy.repository.dao.Association;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.association.AssociationDAORepository;
import com.medvedskiy.repository.repositories.payment.db1.PaymentEntityDB1Repository;
import com.medvedskiy.repository.repositories.payment.db2.PaymentEntityDB2Repository;
import com.medvedskiy.repository.repositories.payment.db3.PaymentEntityDB3Repository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TotalSumService {

    private final AssociationDAORepository associationDAORepository;


    private final PaymentEntityDB1Repository firstDatabasePaymentRepository;

    private final PaymentEntityDB2Repository secondDatabasePaymentRepository;

    private final PaymentEntityDB3Repository thirdDatabasePaymentRepository;


    public TotalSumService(
            AssociationDAORepository associationDAORepository,

            PaymentEntityDB1Repository firstDatabasePaymentRepository,

            PaymentEntityDB2Repository secondDatabasePaymentRepository,

            PaymentEntityDB3Repository thirdDatabasePaymentRepository
    ) {
        this.associationDAORepository = associationDAORepository;
        this.firstDatabasePaymentRepository = firstDatabasePaymentRepository;
        this.secondDatabasePaymentRepository = secondDatabasePaymentRepository;
        this.thirdDatabasePaymentRepository = thirdDatabasePaymentRepository;

    }

    public Long calculateTotalSumBySender(
            Long senderId
    ) {
        Association association = associationDAORepository.findOne(senderId);
        if(association == null) {
            return 0L;
        }
        int dbIndex = association.getDbId();
        List<PaymentEntity> paymentsBySender = Collections.emptyList();
        if(dbIndex == 0) {
            paymentsBySender = firstDatabasePaymentRepository.findPaymentsBySender(senderId);
        }
        else if(dbIndex == 1) {
            paymentsBySender = secondDatabasePaymentRepository.findPaymentsBySender(senderId);
        }
        else if(dbIndex == 2) {
            paymentsBySender = thirdDatabasePaymentRepository.findPaymentsBySender(senderId);
        }
        return paymentsBySender.stream().map(PaymentEntity::getPrice).reduce(0L, Long::sum);
    }
}
