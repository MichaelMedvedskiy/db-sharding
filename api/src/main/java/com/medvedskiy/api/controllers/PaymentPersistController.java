package com.medvedskiy.api.controllers;

import com.medvedskiy.core.models.Payment;
import com.medvedskiy.core.services.ShardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/payment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentPersistController {


    private final ShardingService shardingService;

    public PaymentPersistController(
            ShardingService shardingService
    ) {
        this.shardingService = shardingService;
    }

    @PostMapping
    public ResponseEntity<List<Payment>> getAllFirst(@RequestBody List<Payment> payments){
        shardingService.insertPayments(payments);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        payments
                );
    }
}
