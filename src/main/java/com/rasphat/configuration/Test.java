package com.rasphat.configuration;

import com.rasphat.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Test {

    private static void configure() {
        Properties properties = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);

            // Read the configuration values
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
