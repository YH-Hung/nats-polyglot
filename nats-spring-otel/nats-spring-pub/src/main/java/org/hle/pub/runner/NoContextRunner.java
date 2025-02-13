package org.hle.pub.runner;

import lombok.extern.slf4j.Slf4j;
import org.hle.pub.publisher.JsPublisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nats.nc-publisher", name = "enable", havingValue = "Y")
public class NoContextRunner implements CommandLineRunner {

    private final JsPublisher publisher;

    public NoContextRunner(JsPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Should be no-context hear");
        publisher.publish("top", "dog dog 213");
    }
}
