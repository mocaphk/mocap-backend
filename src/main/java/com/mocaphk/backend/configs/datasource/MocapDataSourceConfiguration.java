package com.mocaphk.backend.configs.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.mocaphk.backend.endpoints.mocap.*",
        entityManagerFactoryRef = "mocapEntityManagerFactory",
        transactionManagerRef = "mocapTransactionManager"
)
@EnableTransactionManagement
public class MocapDataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean(name = "mocapDataSourceProperties")
    @Primary
    @ConfigurationProperties("spring.datasource.mocap")
    public DataSourceProperties mocapDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mocapDataSource")
    @Primary
    public HikariDataSource mocapDataSource(@Qualifier("mocapDataSourceProperties") DataSourceProperties mocapDataSourceProperties) {
        return mocapDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // https://stackoverflow.com/questions/72505311/entitymanager-doesnt-translate-camel-case-to-snake-case
    @Bean(name = "mocapEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean mocapEntityManagerFactory(
            @Qualifier("mocapDataSource") DataSource mocapDataSource,
            EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(mocapDataSource);
        emf.setPackagesToScan("com.mocaphk.backend.endpoints.mocap.*");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(Boolean.parseBoolean(env.getProperty("spring.jpa.show-sql")));
        emf.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        properties.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        emf.setJpaPropertyMap(properties);

        return emf;
    }

    @Bean("mocapTransactionManager")
    @Primary
    public PlatformTransactionManager mocapTransactionManager(
            @Qualifier("mocapEntityManagerFactory") LocalContainerEntityManagerFactoryBean mocapEntityManagerFactory) {
        return new JpaTransactionManager(mocapEntityManagerFactory.getObject());
    }
}
