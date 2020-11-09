package com.medvedskiy.api.controllers;

import com.medvedskiy.core.services.TotalSumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/total/sender", produces = MediaType.APPLICATION_JSON_VALUE)
public class TotalSumController {

    private final TotalSumService totalSumService;

    public TotalSumController(TotalSumService totalSumService) {
        this.totalSumService = totalSumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Long> getTotalSum(@PathVariable("id") Long senderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        totalSumService.calculateTotalSumBySender(senderId)
                );
    }
}
