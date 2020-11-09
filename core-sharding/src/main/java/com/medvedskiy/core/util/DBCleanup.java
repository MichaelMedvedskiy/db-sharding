package com.medvedskiy.core.util;

import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.db1.PaymentEntityDB1Repository;
import com.medvedskiy.repository.repositories.payment.db2.PaymentEntityDB2Repository;
import com.medvedskiy.repository.repositories.payment.db3.PaymentEntityDB3Repository;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class for database cleanup managements
 */
public class DBCleanup implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    PaymentEntityDB1Repository firstDBRepository;
    PaymentEntityDB2Repository secondDBRepository;
    PaymentEntityDB3Repository thirdDBRepository;
    AssociationEntityRepository associationEntityRepository;

    AnnotationConfigApplicationContext context;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        context =
                new AnnotationConfigApplicationContext(
                        extensionContext.getTestClass().orElseThrow().getAnnotation(ContextConfiguration.class).classes()
                );
        firstDBRepository = context.getBean(PaymentEntityDB1Repository.class);
        secondDBRepository = context.getBean(PaymentEntityDB2Repository.class);
        thirdDBRepository = context.getBean(PaymentEntityDB3Repository.class);
        associationEntityRepository = context.getBean(AssociationEntityRepository.class);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        cleanDB();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        cleanDB();
        context.close();
    }

    /**
     * deletes all from all 4 databases
     */
    @Transactional("chainedTransactionManager")
    void cleanDB() {
        firstDBRepository.deleteAll();
        secondDBRepository.deleteAll();
        thirdDBRepository.deleteAll();
        associationEntityRepository.deleteAll();
    }
}
