package com.medvedskiy.repository.repositories.payment.db3;

import com.medvedskiy.repository.dao.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PaymentEntityDB3Repository extends JpaRepository<PaymentEntity, Long> {
    @Query(value = "SELECT p FROM PaymentEntity p WHERE p.sender = ?1")
    List<PaymentEntity> findPaymentsBySender(Long senderId);

    @Query(value = "SELECT distinct (p.sender) from PaymentEntity p")
    Set<Long> findUniqueSenderIds();
}
