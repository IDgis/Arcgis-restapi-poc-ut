package nl.idgis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Annotatie die aangeeft dat het een Spring Boot applicatie is
// vanaf dit punt wordt er automatisch gezocht naar de controllers
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
