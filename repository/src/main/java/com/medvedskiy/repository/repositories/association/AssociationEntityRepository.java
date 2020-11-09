package com.medvedskiy.repository.repositories.association;

import com.medvedskiy.repository.dao.AssociationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssociationEntityRepository extends JpaRepository<AssociationEntity, Long> {

}
