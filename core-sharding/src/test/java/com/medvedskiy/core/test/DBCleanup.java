package com.medvedskiy.core.test;

import com.medvedskiy.repository.repositories.AssociationDAORepository;
import com.medvedskiy.repository.repositories.PaymentEntityRepository;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

public class DBCleanup implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    PaymentEntityRepository firstDBRepository;
    PaymentEntityRepository secondDBRepository;
    PaymentEntityRepository thirdDBRepository;
    AssociationDAORepository associationDBRepository;
    TransactionTemplate firstTemplate;
    TransactionTemplate secondTemplate;
    TransactionTemplate thirdTemplate;
    TransactionTemplate associationTemplate;
    AnnotationConfigApplicationContext context;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        context =
                new AnnotationConfigApplicationContext(
                        extensionContext.getTestClass().orElseThrow().getAnnotation(ContextConfiguration.class).classes()
                );
        firstDBRepository = context.getBean("firstDBRepository", PaymentEntityRepository.class);
        secondDBRepository = context.getBean("secondDBRepository", PaymentEntityRepository.class);
        thirdDBRepository = context.getBean("thirdDBRepository", PaymentEntityRepository.class);
        associationDBRepository = context.getBean("associationDBRepository", AssociationDAORepository.class);
        firstTemplate = context.getBean("firstTransactionTemplate", TransactionTemplate.class);
        secondTemplate = context.getBean("secondTransactionTemplate", TransactionTemplate.class);
        thirdTemplate = context.getBean("thirdTransactionTemplate", TransactionTemplate.class);
        associationTemplate = context.getBean("associationTransactionTemplate", TransactionTemplate.class);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        cleanDB();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        cleanDB();
        context.close();
    }

    void cleanDB() {
        firstTemplate.execute(status ->
        {
            firstDBRepository.deleteAll();
            return null;
        });
        secondTemplate.execute(status ->
        {
            secondDBRepository.deleteAll();
            return null;
        });
        thirdTemplate.execute(status ->
        {
            thirdDBRepository.deleteAll();
            return null;
        });
        associationTemplate.execute(status ->
        {
            associationDBRepository.deleteAll();
            return null;
        });
    }
}
