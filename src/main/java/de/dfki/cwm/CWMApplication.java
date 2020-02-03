package de.dfki.cwm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jumo04 - Julian Moreno Schneider
 * Class executing the spring boot application of the Curation Workflow Manager
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {"de.dfki.cwm"})
public class CWMApplication {

	/**
	 * Main class starting the Spring Application
	 * @param args
	 */
	public static void main(String[] args) {
        SpringApplication.run(CWMApplication.class, args);
    }

}
