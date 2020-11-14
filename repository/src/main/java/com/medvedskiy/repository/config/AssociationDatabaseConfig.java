package com.medvedskiy.repository.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * DB config for Association entities
 *
 */
@Configuration
@PropertySource("classpath:associationDB.properties")
@EnableJpaRepositories(
        basePackages = "com.medvedskiy.repository.repositories.association",
        entityManagerFactoryRef = "associationEntityManager",
        transactionManagerRef = "associationTransactionManager")
@EnableTransactionManagement
public class AssociationDatabaseConfig {

    @Value("${association.db.driver}")
    private String driver;
    @Value("${association.db.url}")
    private String url;
    @Value("${association.db.username}")
    private String username;
    @Value("${association.db.password}")
    private String password;
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.show_sql}")
    private boolean showSQL;
    @Value("${hibernate.format_sql}")
    private boolean formatSQL;
    @Value("${entitymanager.packages.to.scan}")
    private String packageScan;
    @Value("${connection.release_mode}")
    private String releaseMode;


    @Bean
    public DataSource associationDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "associationEntityManager")
    public LocalContainerEntityManagerFactoryBean associationEntityManager(
            @Qualifier("associationDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(packageScan);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }


    @Bean(name = "associationTransactionManager")
    public PlatformTransactionManager associationTransactionManager(
            @Qualifier("associationEntityManager") EntityManagerFactory associationEntityManager
    ) {
        return new JpaTransactionManager(associationEntityManager);

    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.show_sql", showSQL);
        properties.put("hibernate.format_sql", formatSQL);
        properties.put("entitymanager.packages.to.scan", packageScan);
        properties.put("connection.release_mode", releaseMode);
        return properties;
    }
}
