package com.medvedskiy.repository.repositories;

import com.medvedskiy.repository.dao.Association;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public interface AssociationDAORepository extends CrudRepository<Association, Long> {

}
