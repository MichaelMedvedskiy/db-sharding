package com.medvedskiy.repository.tenanting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DynamicTenantAwareRoutingSource extends AbstractRoutingDataSource {

    private final String filename;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, HikariDataSource> tenants;

    public DynamicTenantAwareRoutingSource(String filename) {
        this(filename, new ObjectMapper());
    }

    public DynamicTenantAwareRoutingSource(String filename, ObjectMapper objectMapper) {
        this.filename = filename;
        this.objectMapper = objectMapper;
        this.tenants = getDataSources();
    }

    @Override
    public void afterPropertiesSet() { }

    @Override
    protected DataSource determineTargetDataSource() {
        String lookupKey = (String) determineCurrentLookupKey();
        return tenants.get(lookupKey);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ThreadLocalStorage.getTenantName();
    }

    private ConcurrentMap<String, HikariDataSource> getDataSources() {
        DatabaseConfiguration[] configurations = getDatabaseConfigurations();
        ThreadLocalStorage.setDatabaseCount(configurations.length);

        return Arrays
                .stream(configurations)
                .collect(Collectors.toConcurrentMap(x ->
                        String.valueOf(x.getTenant()), x -> buildDataSource(x)));
    }

    private DatabaseConfiguration[] getDatabaseConfigurations() {
        try {
            return objectMapper.readValue(new ClassPathResource(filename).getFile(), DatabaseConfiguration[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HikariDataSource buildDataSource(DatabaseConfiguration configuration) {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setInitializationFailTimeout(0);
        dataSource.setMaximumPoolSize(5);
        dataSource.setDataSourceClassName(configuration.getDataSourceClassName());
        dataSource.addDataSourceProperty("url", configuration.getUrl());
        dataSource.addDataSourceProperty("user", configuration.getUser());
        dataSource.addDataSourceProperty("password", configuration.getPassword());

        return dataSource;
    }

}
