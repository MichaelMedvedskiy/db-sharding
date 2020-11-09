package com.medvedskiy.api.controllers;

import com.medvedskiy.core.models.TotalSumWrapper;
import com.medvedskiy.core.services.TotalSumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for getting
 */
@RestController
@RequestMapping(value = "/api/v1/total/sender", produces = MediaType.APPLICATION_JSON_VALUE)
public class TotalSumController {
    Logger log = LoggerFactory.getLogger(TotalSumController.class);

    private final TotalSumService totalSumService;

    public TotalSumController(TotalSumService totalSumService) {
        this.totalSumService = totalSumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TotalSumWrapper> getTotalSum(@PathVariable("id") Long senderId) {
        log.info("Calculating total sum for sender id: {}", senderId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        TotalSumWrapper.builder()
                                .totalSum(
                                        totalSumService.calculateTotalSumBySender(senderId)
                                )
                                .build()
                );
    }
}
