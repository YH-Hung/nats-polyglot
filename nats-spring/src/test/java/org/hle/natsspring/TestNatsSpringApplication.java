package org.hle.natsspring;

import org.springframework.boot.SpringApplication;

public class TestNatsSpringApplication {

    public static void main(String[] args) {
        SpringApplication.from(NatsSpringApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
