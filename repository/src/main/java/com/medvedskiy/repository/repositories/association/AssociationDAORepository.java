package com.medvedskiy.repository.repositories.association;

import com.medvedskiy.repository.dao.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssociationDAORepository extends JpaRepository<Association, Long> {

}
