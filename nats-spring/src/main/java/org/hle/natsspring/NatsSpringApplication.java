package org.hle.natsspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NatsSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(NatsSpringApplication.class, args);
    }

}
