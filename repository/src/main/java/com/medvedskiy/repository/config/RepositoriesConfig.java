package com.medvedskiy.repository.config;

import com.medvedskiy.repository.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.medvedskiy")
@EnableJpaRepositories(
        basePackages = "com.medvedskiy.repository",
        entityManagerFactoryRef = "firstEntityManager",
        transactionManagerRef = "firstTransactionManager")
public class RepositoriesConfig {


    private final EntityManager firstManager;


    private final EntityManager secondManager;


    private final EntityManager thirdManager;

    public RepositoriesConfig(
            @Qualifier("firstEntityManager") EntityManager firstManager,
            @Qualifier("secondEntityManager") EntityManager secondManager,
            @Qualifier("thirdEntityManager") EntityManager thirdManager
            ) {
        this.firstManager = firstManager;
        this.secondManager = secondManager;
        this.thirdManager = thirdManager;
    }

    @Bean(name = "firstDBRepository")
    @Qualifier("firstDBRepository")
    public PersonRepository firstRepository() {
        return new JpaRepositoryFactory(firstManager).getRepository(PersonRepository.class);
    }

    @Bean(name = "secondDBRepository")
    @Qualifier("secondDBRepository")
    public PersonRepository secondRepository() {
        return new JpaRepositoryFactory(secondManager).getRepository(PersonRepository.class);
    }

    @Bean(name = "thirdDBRepository")
    @Qualifier("thirdDBRepository")
    public PersonRepository thirdRepository() {
        return new JpaRepositoryFactory(thirdManager).getRepository(PersonRepository.class);
    }
}
