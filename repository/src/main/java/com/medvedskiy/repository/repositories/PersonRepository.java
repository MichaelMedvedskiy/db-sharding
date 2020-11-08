package com.medvedskiy.repository.repositories;

import com.medvedskiy.repository.dao.PersonDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface PersonRepository extends CrudRepository<PersonDAO, Long> {
}
