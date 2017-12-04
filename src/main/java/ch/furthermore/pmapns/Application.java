package ch.furthermore.pmapns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Build Samples:
 * <pre>
 * mvn package docker:build
 * docker push dockerchtz/pmapns:latest
 * </pre>
 * 
 * Run Samples:
 * <pre>
 * docker run -p 6060:6060 -d dockerchtz/pmapns
 * </pre>
 */
@SpringBootApplication
public class Application {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
