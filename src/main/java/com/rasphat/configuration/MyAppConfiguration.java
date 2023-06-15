package com.rasphat.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class MyAppConfiguration {
    private String logDirectory;
    private String username;
    private String password;

    // Getter und Setter für die Konfigurationseigenschaften

    public String getLogDirectory() {
        return logDirectory;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


}
