package in.koreatech.payment.common.config.dataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource.koin.hibernate")
public record KoinDBProperties(
    String ddlAuto,
    Boolean showSql,
    String packagesToScan
) {
}
