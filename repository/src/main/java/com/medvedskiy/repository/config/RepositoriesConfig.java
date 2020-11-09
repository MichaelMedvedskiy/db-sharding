package com.medvedskiy.repository.config;

import com.medvedskiy.repository.repositories.AssociationDAORepository;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.medvedskiy")

public class RepositoriesConfig {


    private final EntityManager firstManager;

    private final EntityManager secondManager;

    private final EntityManager thirdManager;

    private final EntityManager associationEntityManager;

    public RepositoriesConfig(
            @Qualifier("firstEntityManager") EntityManager firstManager,
            @Qualifier("secondEntityManager") EntityManager secondManager,
            @Qualifier("thirdEntityManager") EntityManager thirdManager,
            @Qualifier("associationEntityManager") EntityManager associationEntityManager
            ) {
        this.firstManager = firstManager;
        this.secondManager = secondManager;
        this.thirdManager = thirdManager;
        this.associationEntityManager = associationEntityManager;
    }

    @Transactional
    @Bean(name = "firstDBRepository")
    @Qualifier("firstDBRepository")
    public PaymentEntityRepository firstRepository() {
        return new JpaRepositoryFactory(firstManager).getRepository(PaymentEntityRepository.class);
    }

    @Transactional
    @Bean(name = "secondDBRepository")
    @Qualifier("secondDBRepository")
    public PaymentEntityRepository secondRepository() {
        return new JpaRepositoryFactory(secondManager).getRepository(PaymentEntityRepository.class);
    }

    @Transactional
    @Bean(name = "thirdDBRepository")
    @Qualifier("thirdDBRepository")
    public PaymentEntityRepository thirdRepository() {
        return new JpaRepositoryFactory(thirdManager).getRepository(PaymentEntityRepository.class);
    }

    @Transactional
    @Bean(name = "associationDBRepository")
    @Qualifier("associationDBRepository")
    public AssociationDAORepository associationRepository() {
        return new JpaRepositoryFactory(associationEntityManager).getRepository(AssociationDAORepository.class);
    }
}
