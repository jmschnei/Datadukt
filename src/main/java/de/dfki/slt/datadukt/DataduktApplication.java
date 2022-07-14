package de.dfki.slt.datadukt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jumo04 - Julian Moreno Schneider
 * Class executing the spring boot application of the Datadukt tool
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {"de.dfki.slt.datadukt"})
public class DataduktApplication {

	/**
	 * Main class starting the Spring Application
	 * @param args
	 */
	public static void main(String[] args) {
        SpringApplication.run(DataduktApplication.class, args);
    }

}
