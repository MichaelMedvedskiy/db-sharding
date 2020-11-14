package com.medvedskiy.repository.config;

import com.medvedskiy.repository.tenanting.DynamicTenantAwareRoutingSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.medvedskiy.repository.repositories.payment",
        entityManagerFactoryRef = "paymentEntityManager",
        transactionManagerRef = "paymentTransactionManager")
@PropertySource({"classpath:hibernate.properties"})
public class PaymentConfig {

    @Value("${dialect}")
    private String dialect;
    @Value("${showSql}")
    private String showSql;
    @Value("${formatSql}")
    private String formatSql;
    @Value("${packages}")
    private String packages;
    @Value("${releaseMode}")
    private String releaseMode;

    @Bean
    public AbstractRoutingDataSource abstractRoutingDataSource() {
        return new DynamicTenantAwareRoutingSource("tenants.json");
    }

    @Bean
    public DataSource paymentDataSource(
            AbstractRoutingDataSource dataSource
    ) {
        dataSource.afterPropertiesSet();
        return dataSource;
    }


    @Bean(name = "paymentEntityManager")
    public LocalContainerEntityManagerFactoryBean paymentEntityManager(
            @Qualifier("paymentDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.medvedskiy.repository.dao.payment");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }


    @Bean(name = "paymentTransactionManager")
    public PlatformTransactionManager paymentTransactionManager(
            @Qualifier("paymentEntityManager") EntityManagerFactory paymentEntityManager
    ) {
        return new JpaTransactionManager(paymentEntityManager);

    }


    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", formatSql);
        properties.put("entitymanager.packages.to.scan", packages);
        properties.put("connection.release_mode", releaseMode);
        return properties;
    }
}
