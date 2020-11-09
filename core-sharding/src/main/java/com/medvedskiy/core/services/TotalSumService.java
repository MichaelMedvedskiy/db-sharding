package com.medvedskiy.core.services;

import com.medvedskiy.repository.dao.Association;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.AssociationDAORepository;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TotalSumService {

    private final AssociationDAORepository associationDAORepository;


    private final PaymentEntityRepository firstDatabasePaymentRepository;

    private final PaymentEntityRepository secondDatabasePaymentRepository;

    private final PaymentEntityRepository thirdDatabasePaymentRepository;


    public TotalSumService(
            @Qualifier("associationDBRepository")
                    AssociationDAORepository associationDAORepository,
            @Qualifier("firstDBRepository")
                    PaymentEntityRepository firstDatabasePaymentRepository,
            @Qualifier("secondDBRepository")
                    PaymentEntityRepository secondDatabasePaymentRepository,
            @Qualifier("thirdDBRepository")
                    PaymentEntityRepository thirdDatabasePaymentRepository
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
