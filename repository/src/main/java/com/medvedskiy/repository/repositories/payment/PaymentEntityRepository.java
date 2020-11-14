package com.medvedskiy.repository.repositories.payment;

import com.medvedskiy.repository.dao.payment.PaymentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface PaymentEntityRepository extends CrudRepository<PaymentEntity, Long> {
    @Query(value = "SELECT p FROM PaymentEntity p WHERE p.sender = ?1")
    List<PaymentEntity> findPaymentsBySender(Long senderId);

    @Query(value = "SELECT distinct (p.sender) from PaymentEntity p")
    Set<Long> findUniqueSenderIds();

}
