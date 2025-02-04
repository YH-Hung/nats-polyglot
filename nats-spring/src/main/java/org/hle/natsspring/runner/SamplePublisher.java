package org.hle.natsspring.runner;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class SamplePublisher implements CommandLineRunner {

    private final Connection nc;

    public SamplePublisher(Connection nc) {
        this.nc = nc;
    }

    @Override
    public void run(String... args) throws Exception {
        JetStream js = nc.jetStream();

        log.info("Start to publish girls messages");

        js.publish("girls.top", "wan".getBytes());
        js.publish("girls.top", "chi han".getBytes());
        js.publish("girls.normal", "pan chin".getBytes());

        log.info("Girl message published");
    }
}
