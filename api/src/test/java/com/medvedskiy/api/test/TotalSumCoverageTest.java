package com.medvedskiy.api.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medvedskiy.api.ApiRunner;
import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.core.models.Payment;
import com.medvedskiy.core.models.TotalSumWrapper;
import com.medvedskiy.core.util.BeanInjector;
import com.medvedskiy.core.util.DBCleanup;
import com.medvedskiy.core.util.FileAsString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Coverage test for sharding + retrieving total sum of sender
 */
@ContextConfiguration(classes = {CoreConfig.class})
@ExtendWith({
        DBCleanup.class,
        BeanInjector.class})
public class TotalSumCoverageTest {

    @Test
    public void testTotalSumApi(
            ObjectMapper mapper
    ) throws IOException, InterruptedException {
        ApplicationContext context = ApiRunner.start();
        //so context was able to start
        Thread.sleep(2000);

        String json = FileAsString.getFile("payments2.json");

        List<Payment> payments = mapper.readValue(json, new TypeReference<List<Payment>>() {});

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<List<Payment>> request = new HttpEntity<>(payments);
        restTemplate.postForObject("http://localhost:8080/api/v1/payment", request, Object.class);


        ResponseEntity<TotalSumWrapper> sumForSenderIdMinus1
                = restTemplate.getForEntity("http://localhost:8080/api/v1/total/sender" + "/-1", TotalSumWrapper.class);
        Assertions.assertEquals(sumForSenderIdMinus1.getBody().totalSum(), 0);

        ResponseEntity<TotalSumWrapper> sumForSenderId15
                = restTemplate.getForEntity("http://localhost:8080/api/v1/total/sender" + "/15", TotalSumWrapper.class);
        Assertions.assertEquals(sumForSenderId15.getBody().totalSum(), 1435);

        ResponseEntity<TotalSumWrapper> sumForSenderId99999
                = restTemplate.getForEntity("http://localhost:8080/api/v1/total/sender" + "/99999", TotalSumWrapper.class);
        Assertions.assertEquals(sumForSenderId99999.getBody().totalSum(), 0);

        System.out.println(123);

    }
}
