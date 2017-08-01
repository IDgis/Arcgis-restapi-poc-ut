package nl.idgis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"nl.idgis", "nl.idgis.query"})
public class Application {
	
	public Application() {
		// Empty constructor
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
