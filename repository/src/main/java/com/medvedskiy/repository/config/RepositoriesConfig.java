package com.medvedskiy.repository.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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

//    @Bean(name = "firstDBRepository")
//    @Qualifier("firstDBRepository")
//    public PaymentEntityRepository firstRepository() {
//        return new JpaRepositoryFactory(firstManager).getRepository(PaymentEntityRepository.class);
//    }
//
//    @Bean(name = "secondDBRepository")
//    @Qualifier("secondDBRepository")
//    public PaymentEntityRepository secondRepository() {
//        return new JpaRepositoryFactory(secondManager).getRepository(PaymentEntityRepository.class);
//    }
//
//    @Bean(name = "thirdDBRepository")
//    @Qualifier("thirdDBRepository")
//    public PaymentEntityRepository thirdRepository() {
//        return new JpaRepositoryFactory(thirdManager).getRepository(PaymentEntityRepository.class);
//    }
//
//    @Bean(name = "associationDBRepository")
//    @Qualifier("associationDBRepository")
//    public AssociationDAORepository associationRepository() {
//        return new JpaRepositoryFactory(associationEntityManager).getRepository(AssociationDAORepository.class);
//    }
}
