package in.koreatech.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class KoinPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(KoinPaymentApplication.class, args);
	}

}
