package com.medvedskiy.repository;

import com.medvedskiy.repository.dao.PersonDAO;
import com.medvedskiy.repository.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
//@RequestMapping(value = "/multiperson", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {
    private PersonRepository firstRepository;

    private  PersonRepository secondRepository;

    private  PersonRepository thirdRepository;

    public TestController(
            @Qualifier("firstDBRepository") PersonRepository firstDBRepository,
            @Qualifier("secondDBRepository") PersonRepository secondDBRepository,
            @Qualifier("thirdDBRepository") PersonRepository thirdDBRepository
    ) {
        this.firstRepository = firstDBRepository;
        this.secondRepository = secondDBRepository;
        this.thirdRepository = thirdDBRepository;
    }

    @GetMapping("/first")
    public ResponseEntity<List<PersonDAO>> getAllFirst(){
        List<PersonDAO> list = (List<PersonDAO>) firstRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        list
                );
    }

    @GetMapping("/second")
    public Iterable<PersonDAO> getAllSecond(){
        return secondRepository.findAll();
    }
    @GetMapping("/third")
    public Iterable<PersonDAO> getAllThird(){
        return thirdRepository.findAll();
    }
}
