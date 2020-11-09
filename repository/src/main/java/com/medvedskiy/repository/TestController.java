package com.medvedskiy.repository;

import com.medvedskiy.repository.dao.PaymentEntity;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


public class TestController {
    private PaymentEntityRepository firstRepository;

    private PaymentEntityRepository secondRepository;

    private PaymentEntityRepository thirdRepository;

    public TestController(
            @Qualifier("firstDBRepository") PaymentEntityRepository firstDBRepository,
            @Qualifier("secondDBRepository") PaymentEntityRepository secondDBRepository,
            @Qualifier("thirdDBRepository") PaymentEntityRepository thirdDBRepository
    ) {
        this.firstRepository = firstDBRepository;
        this.secondRepository = secondDBRepository;
        this.thirdRepository = thirdDBRepository;
    }

    @GetMapping("/first")
    public ResponseEntity<List<PaymentEntity>> getAllFirst(){
        List<PaymentEntity> list = (List<PaymentEntity>) firstRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        list
                );
    }

    @GetMapping("/second")
    public Iterable<PaymentEntity> getAllSecond(){
        return secondRepository.findAll();
    }
    @GetMapping("/third")
    public Iterable<PaymentEntity> getAllThird(){
        return thirdRepository.findAll();
    }
}
