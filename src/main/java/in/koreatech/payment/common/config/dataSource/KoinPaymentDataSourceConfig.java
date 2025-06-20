package in.koreatech.payment.common.config.dataSource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    basePackages = "in.koreatech.payment",
    entityManagerFactoryRef = "koinPaymentEntityManagerFactory",
    transactionManagerRef = "koinPaymentTransactionManager"
)
public class KoinPaymentDataSourceConfig {

    private final KoinPaymentDBProperties koinPaymentDBProperties;

    @Bean(name = "koinPaymentDataSource")
    @ConfigurationProperties("spring.datasource.koin-payment")
    public DataSource koinPaymentDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "koinPaymentEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean koinPaymentEntityManagerFactory(
        @Qualifier(value = "koinPaymentDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(koinPaymentDBProperties.packagesToScan());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(createJpaVendorProperties());
        return em;
    }

    private Map<String, Object> createJpaVendorProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", koinPaymentDBProperties.showSql());
        properties.put("hibernate.hbm2ddl.auto", koinPaymentDBProperties.ddlAuto());
        return properties;
    }

    @Primary
    @Bean(name = "koinPaymentTransactionManager")
    public PlatformTransactionManager koinPaymentTransactionManager(
        @Qualifier(value = "koinPaymentEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
