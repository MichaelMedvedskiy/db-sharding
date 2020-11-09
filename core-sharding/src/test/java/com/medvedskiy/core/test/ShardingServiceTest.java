package com.medvedskiy.core.test;

import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.core.models.Payment;
import com.medvedskiy.core.services.ShardingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(classes = {CoreConfig.class})
@ExtendWith({
        //DBCleanup.class,
        BeanInjector.class})
public class ShardingServiceTest {

    public void testSharding(
       ShardingService shardingService
    ){
        Payment payment = Payment.builder()
                .price(1L)
                .receiver(1L)
                .sender(1L)
                .build();
        Payment payment2 = Payment.builder()
                .price(3L)
                .receiver(2L)
                .sender(1L)
                .build();
        Payment payment3 = Payment.builder()
                .price(4L)
                .receiver(3L)
                .sender(2L)
                .build();
        shardingService.insertPayments(List.of(payment, payment2, payment3));
        System.out.println(1231);
    }
}
