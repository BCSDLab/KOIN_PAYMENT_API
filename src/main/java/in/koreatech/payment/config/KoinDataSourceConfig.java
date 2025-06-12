package in.koreatech.payment.config;

import java.util.HashMap;

import javax.sql.DataSource;

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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "in.koreatech.koin",
    entityManagerFactoryRef = "koinEntityManagerFactory",
    transactionManagerRef = "koinTransactionManager"
)
public class KoinDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.koin")
    public DataSource koinDataDBSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean koinEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(koinDataDBSource());
        em.setPackagesToScan("in.koreatech.koin");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager koinTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(koinEntityManagerFactory().getObject());
        return transactionManager;
    }
}
