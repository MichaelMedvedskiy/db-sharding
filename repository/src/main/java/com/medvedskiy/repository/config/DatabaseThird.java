package com.medvedskiy.repository.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:database.properties")
@EnableJpaRepositories(
        basePackages = "com.medvedskiy.repository",
        entityManagerFactoryRef = "thirdEntityManager",
        transactionManagerRef = "thirdTransactionManager")
public class DatabaseThird {

    @Value("${third.db.driver}")
    private String driver;
    @Value("${third.db.url}")
    private String url;
    @Value("${third.db.username}")
    private String username;
    @Value("${third.db.password}")
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

    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "thirdEntityManager")
    public LocalContainerEntityManagerFactoryBean thirdEntityManager(
            @Qualifier("thirdDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(new String[] { packageScan });
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    @Bean(name = "thirdTransactionTemplate")
    public TransactionTemplate transactionTemplate(
            @Qualifier("thirdDataSource") DataSource dataSource
    ) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager(
            @Qualifier("thirdEntityManager") LocalContainerEntityManagerFactoryBean entityManager
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        return transactionManager;
    }

    @Bean(name = "thirdSessionFactory")
    public LocalSessionFactoryBean thirdSessionFactory(
            @Qualifier("thirdDataSource") DataSource dataSource
    ) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(packageScan);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        return sessionFactoryBean;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", false);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.show_sql",showSQL);
        properties.put("hibernate.format_sql",formatSQL);
        properties.put("entitymanager.packages.to.scan",packageScan);
        properties.put("connection.release_mode",releaseMode);
        return properties;
    }
}
