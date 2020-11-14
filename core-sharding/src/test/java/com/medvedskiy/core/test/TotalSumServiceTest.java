package com.medvedskiy.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.core.exceptions.UndefinedBehaviorException;
import com.medvedskiy.core.models.Payment;
import com.medvedskiy.core.services.ShardingService;
import com.medvedskiy.core.services.TotalSumService;
import com.medvedskiy.tests.BeanInjector;
import com.medvedskiy.tests.DBCleanup;
import com.medvedskiy.tests.FileAsString;
import com.medvedskiy.tests.config.TestPaymentDatasourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;

@ContextConfiguration(classes = {CoreConfig.class, TestPaymentDatasourceConfig.class})
@ExtendWith({
        DBCleanup.class,
        BeanInjector.class})
public class TotalSumServiceTest {


    public static long rnd(long max) {
        return (long) (Math.random() * max);
    }

    /**
     * Static test for sum of all payments by Id, initialized by payments.json
     *
     * @param shardingService - for persist.
     * @param totalSumService - for getting total sum.
     * @param mapper          - for deserializing list of Payments
     */
    @Test
    public void testTotalSum(
            ShardingService shardingService,
            TotalSumService totalSumService,
            ObjectMapper mapper
    ) throws UndefinedBehaviorException, IOException {

        String json = FileAsString.getFile("payments.json");
        List<Payment> paymentList = mapper.readValue(json, new TypeReference<List<Payment>>() {});
        shardingService.insertPayments(paymentList);

        long sumForSenderIdMinus1 = totalSumService.calculateTotalSumBySender(-1L);
        Assertions.assertEquals(sumForSenderIdMinus1, 0);
        long sumForSenderId15 = totalSumService.calculateTotalSumBySender(15L);
        Assertions.assertEquals(sumForSenderId15, 1435);
        long sumForSenderId99999 = totalSumService.calculateTotalSumBySender(99999L);
        Assertions.assertEquals(sumForSenderId99999, 0);

    }

}
