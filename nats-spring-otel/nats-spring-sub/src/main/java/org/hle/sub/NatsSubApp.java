package org.hle.sub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NatsSubApp      {
    public static void main(String[] args) {
        SpringApplication.run(NatsSubApp.class, args);
    }
}