package in.koreatech.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConfigurationPropertiesScan
@SpringBootApplication(
	scanBasePackages = {
		"in.koreatech.payment",
		"in.koreatech.koin"
	}
)
@EnableJpaRepositories(basePackages = {
	"in.koreatech.payment",
	"in.koreatech.koin"
})
@EntityScan(basePackages = {
	"in.koreatech.payment",
	"in.koreatech.koin"
})
public class KoinPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(KoinPaymentApplication.class, args);
	}

}
