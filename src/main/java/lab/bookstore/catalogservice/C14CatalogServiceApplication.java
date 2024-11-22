package lab.bookstore.catalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class C14CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(C14CatalogServiceApplication.class, args);
	}

}
