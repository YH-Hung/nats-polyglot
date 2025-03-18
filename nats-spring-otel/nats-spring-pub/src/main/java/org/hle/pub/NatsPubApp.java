package org.hle.pub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NatsPubApp {
    public static void main(String[] args) {
        SpringApplication.run(NatsPubApp.class, args);
    }
}