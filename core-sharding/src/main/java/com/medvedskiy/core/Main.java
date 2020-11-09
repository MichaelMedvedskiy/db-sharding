package com.medvedskiy.core;

import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.core.exceptions.UndefinedBehaviorException;
import com.medvedskiy.core.services.ShardingService;
import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Transactional
public class Main {
    public static void main(String[] args) throws UndefinedBehaviorException {

        ApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class);

        ShardingService shardingService = context.getBean(ShardingService.class);
        PaymentEntityRepository paymentEntityRepository = context.getBean("firstDBRepository", PaymentEntityRepository.class);
        PaymentEntity paymentEntityDAO = new PaymentEntity();
        paymentEntityDAO.setSender(1337L);
        paymentEntityDAO.setReceiver(1338L);
        paymentEntityDAO.setPrice(13333337L);
        TransactionTemplate transactionTemplate = context.getBean("firstTransactionTemplate", TransactionTemplate.class);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        transactionTemplate.execute(status -> {paymentEntityRepository.save(paymentEntityDAO); return  null;});
        com.medvedskiy.core.models.Payment payment = com.medvedskiy.core.models.Payment.builder()
                .price(1L)
                .receiver(1L)
                .sender(1L)
                .build();
        com.medvedskiy.core.models.Payment payment2 = com.medvedskiy.core.models.Payment.builder()
                .price(3L)
                .receiver(2L)
                .sender(1L)
                .build();
        com.medvedskiy.core.models.Payment payment3 = com.medvedskiy.core.models.Payment.builder()
                .price(4L)
                .receiver(3L)
                .sender(2L)
                .build();
        shardingService.insertPayments(List.of(payment, payment2, payment3));
    }
//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class);
//        ShardingService shardingService = context.getBean(ShardingService.class);
//        Payment payment = Payment.builder()
//                .price(1L)
//                .receiver(1L)
//                .sender(1L)
//                .build();
//        Payment payment2 = Payment.builder()
//                .price(3L)
//                .receiver(2L)
//                .sender(1L)
//                .build();
//        Payment payment3 = Payment.builder()
//                .price(4L)
//                .receiver(3L)
//                .sender(2L)
//                .build();
//        shardingService.insertPayments(List.of(payment, payment2, payment3));
//    }
}
