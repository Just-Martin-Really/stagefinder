package de.dhbwravensburg.webeng.stagefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StagefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StagefinderApplication.class, args);
    }

}
