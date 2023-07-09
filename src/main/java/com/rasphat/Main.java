package com.rasphat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of the application.
 * This class serves as the entry point for the application and initializes the Spring Boot framework.
 * It is responsible for starting up the application and launching the Spring Boot runtime environment.
 * The class is annotated with @SpringBootApplication, which combines three annotations:
 * - @Configuration: Indicates that this class provides Spring Boot configuration.
 * - @EnableAutoConfiguration: Enables Spring Boot's autoconfiguration feature that automatically configures the application based on dependencies and settings.
 * - @ComponentScan: Instructs Spring Boot to scan and detect components, services, and controllers in the specified package and its sub-packages.
 */
@SpringBootApplication
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * The main() method is the starting point of the application.
     * It calls the SpringApplication.run() method, passing the Main class as an argument.
     * This method starts the Spring Boot application context and launches the embedded web server to serve the application.
     * To run the application, simply execute the main() method.
     * Spring Boot will take care of the rest, handling the application's lifecycle and managing the various components and dependencies.
     * Note: Make sure the required dependencies are properly configured in the project's build configuration to ensure the successful execution of the application.
     *
     * @param args The command-line arguments passed to the application (not used in this example).
     */
    public static void main(String[] args) {
        LOGGER.info("Application initializing...");
        SpringApplication.run(Main.class);
        LOGGER.info("Application runs and runs and runs");
    }
}