package org.hle.pub.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hle.pub.publisher.JsPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GirlListener {

    private final JsPublisher publisher;

    public GirlListener(JsPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @KafkaListener(topics = "${kafka.girls.topic-name}")
    public void process(String message) {
        log.info("Get message from kafka: {}, transfer to nats", message);
        publisher.publish("top", message);
    }
}
