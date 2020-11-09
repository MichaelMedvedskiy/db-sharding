package com.medvedskiy.repository.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import({DatabaseFirst.class, DatabaseSecond.class, DatabaseThird.class, AssociationDatabaseConfig.class})
public class ChainedTransactionConfig {

    @Primary
    @Bean(name = "chainedTransactionManager")
    public ChainedTransactionManager transactionManager(
            @Qualifier("associationTransactionManager") PlatformTransactionManager ds1,
            @Qualifier("firstTransactionManager") PlatformTransactionManager ds2,
            @Qualifier("secondTransactionManager") PlatformTransactionManager ds3,
            @Qualifier("thirdTransactionManager") PlatformTransactionManager ds4
    ) {
        return new ChainedTransactionManager(ds1, ds2, ds3, ds4);
    }
}
