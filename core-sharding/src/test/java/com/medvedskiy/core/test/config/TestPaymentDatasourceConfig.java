package com.medvedskiy.core.test.config;

import com.medvedskiy.repository.tenanting.DynamicTenantAwareRoutingSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class TestPaymentDatasourceConfig {
    @Bean
    @Primary
    public AbstractRoutingDataSource abstractRoutingDataSource() {
        return new DynamicTenantAwareRoutingSource("testTenants.json");
    }
}
