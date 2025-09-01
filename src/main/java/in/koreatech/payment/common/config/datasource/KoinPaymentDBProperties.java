package in.koreatech.payment.common.config.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource.koin-payment.hibernate")
public record KoinPaymentDBProperties(
    String ddlAuto,
    Boolean showSql,
    String packagesToScan,
    String formatSql
) {

}
