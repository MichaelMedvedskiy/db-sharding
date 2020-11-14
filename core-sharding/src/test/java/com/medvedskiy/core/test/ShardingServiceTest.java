package com.medvedskiy.core.test;

import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.core.models.Payment;
import com.medvedskiy.core.services.ShardingService;
import com.medvedskiy.core.util.BeanInjector;
import com.medvedskiy.core.util.DBCleanup;
import com.medvedskiy.repository.repositories.payment.PaymentEntityRepository;
import com.medvedskiy.repository.tenanting.ThreadLocalStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ContextConfiguration(classes = {CoreConfig.class})
@ExtendWith({
        DBCleanup.class,
        BeanInjector.class})
public class ShardingServiceTest {

    public static long rnd(long max) {
        return (long) (Math.random() * max);
    }

    /**
     * Best way to see if sharding by sender was successful - perform sharding many times with different inputs,
     * see whether each database has a unique set of sender ids
     *
     * @param shardingService service for sharding test
     */
    @Test
    public void testSharding(
            ShardingService shardingService,
            PaymentEntityRepository paymentEntityRepository
    ) throws Exception {
        //todo: from env
        int databaseCount = ThreadLocalStorage.getDatabaseCount();

        for (long i = 0; i < rnd(99) + 10; i++) {
            shardingService.insertPayments(rndPaymentList());
        }
        List<Set<Long>> idsInEachDb = new ArrayList<>();
        for (int i = 0; i < databaseCount; i++) {
            ThreadLocalStorage.setTenantName(String.valueOf(i));
            idsInEachDb.add(paymentEntityRepository.findUniqueSenderIds());
        }

        int totalItemCount = 0;
        HashSet<Long> allSetsMerged = new HashSet<>();
        for (Set<Long> set : idsInEachDb) {
            for (Long item : set) {
                totalItemCount += 1;
                allSetsMerged.add(item);
            }
        }
        if (totalItemCount != allSetsMerged.size()) {
            throw new Exception("Total count of IDs and count of unique ids from all DBs did not match, sharding failed");
        }


        //todo: database count from tenants, add DBCleanup, MOOOOVE
//        if (idsFromDB1.stream().anyMatch(idsFromDB2::contains) || idsFromDB2.stream().anyMatch(idsFromDB3::contains)) {
//            throw new RuntimeException("Failed Sharding");
//        }
        System.out.println(1231);
    }

    public List<Payment> rndPaymentList() {
        List<Payment> payments = new ArrayList<>();
        for (long i = 0; i < rnd(1000) + 10; i++) {
            payments.add(rndPayment(rnd(777)));
        }
        return payments;
    }

    public Payment rndPayment(long senderIdUpperCap) {
        return Payment.builder()
                .price(rnd(333))
                .receiver(rnd(111))
                .sender(rnd(senderIdUpperCap))
                .build();
    }

}
