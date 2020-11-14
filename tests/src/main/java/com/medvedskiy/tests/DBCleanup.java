package com.medvedskiy.tests;

import com.medvedskiy.repository.repositories.association.AssociationEntityRepository;
import com.medvedskiy.repository.repositories.payment.PaymentEntityRepository;
import com.medvedskiy.repository.tenanting.ThreadLocalStorage;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Class for database cleanup managements
 */
public class DBCleanup implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    PaymentEntityRepository paymentEntityRepository;
    AssociationEntityRepository associationEntityRepository;

    AnnotationConfigApplicationContext context;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        context =
                new AnnotationConfigApplicationContext(
                        extensionContext.getTestClass().orElseThrow().getAnnotation(ContextConfiguration.class).classes()
                );
        paymentEntityRepository = context.getBean(PaymentEntityRepository.class);
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
    void cleanDB() {
        int databaseCount = ThreadLocalStorage.getDatabaseCount();
        for (int i = 0; i < databaseCount; i++) {
            ThreadLocalStorage.setTenantName(String.valueOf(i));
            paymentEntityRepository.deleteAll();
        }

        associationEntityRepository.deleteAll();
    }
}
