package in.koreatech.payment.common.config.dataSource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "in.koreatech.koin",
    entityManagerFactoryRef = "koinEntityManagerFactory",
    transactionManagerRef = "koinTransactionManager"
)
public class KoinDataSourceConfig {

    private final KoinDBProperties koinDBProperties;

    @Bean(name = "koinDataSource")
    @ConfigurationProperties("spring.datasource.koin")
    public DataSource koinDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "koinEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean koinEntityManagerFactory(
        @Qualifier(value = "koinDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(koinDBProperties.packagesToScan());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(createJpaVendorProperties());
        return em;
    }

    private Map<String, Object> createJpaVendorProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", koinDBProperties.showSql());
        properties.put("hibernate.hbm2ddl.auto", koinDBProperties.ddlAuto());
        return properties;
    }

    @Bean(name = "koinTransactionManager")
    public PlatformTransactionManager koinTransactionManager(
        @Qualifier(value = "koinEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
