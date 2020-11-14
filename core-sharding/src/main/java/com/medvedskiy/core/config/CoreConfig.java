package com.medvedskiy.core.config;

import com.medvedskiy.repository.config.AssociationDatabaseConfig;
import com.medvedskiy.repository.config.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.medvedskiy.core.services")
@Import({AssociationDatabaseConfig.class, PaymentConfig.class})
public class CoreConfig {
}
