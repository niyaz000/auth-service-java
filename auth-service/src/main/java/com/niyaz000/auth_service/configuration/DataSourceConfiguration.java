package com.niyaz000.auth_service.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${admin.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSizeAdminDs;

    @Bean
    @ConfigurationProperties(prefix = "admin.datasource")
    public DataSourceProperties adminDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "adminDataSource")
    public DataSource adminDataSource() {
        HikariDataSource ds = adminDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setMaximumPoolSize(maximumPoolSizeAdminDs);
        return ds;
    }

    @Bean
    public Map<Object, Object> dataSourceTargets() {
        return new HashMap<>();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource defaultDs = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        defaultDs.setMaximumPoolSize(maximumPoolSize);

        TenantAwareDataSource routingDs = new TenantAwareDataSource();
        routingDs.setDefaultTargetDataSource(defaultDs);
        routingDs.setTargetDataSources(dataSourceTargets());
        routingDs.setLenientFallback(true);
        routingDs.afterPropertiesSet();

        return routingDs;
    }
}
