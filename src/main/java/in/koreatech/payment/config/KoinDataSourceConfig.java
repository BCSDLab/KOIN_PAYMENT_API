package in.koreatech.payment.config;

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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "in.koreatech.koin",
    entityManagerFactoryRef = "koinEntityManagerFactory",
    transactionManagerRef = "koinTransactionManager"
)
public class KoinDataSourceConfig {

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
        em.setPackagesToScan("in.koreatech.koin");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean(name = "koinTransactionManager")
    public PlatformTransactionManager koinTransactionManager(
        @Qualifier(value = "koinEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
