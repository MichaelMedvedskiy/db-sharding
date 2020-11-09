package com.medvedskiy.repository.repositories;

import com.medvedskiy.repository.dao.PaymentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface PaymentEntityRepository extends CrudRepository<PaymentEntity, Long> {
    @Query(value = "SELECT p FROM PaymentEntity p WHERE p.sender = ?1")
    List<PaymentEntity> findPaymentsBySender(Long senderId);

}
