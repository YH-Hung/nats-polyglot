package org.hle.natsspring.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "msg.listener", name = "type", havingValue = "kafka")
public class KafkaMsgListener {

    @KafkaListener(topics = "${kafka.girls.topic-name}")
    public void processMessage(String content) {
        log.info("Get message from kafka: {}", content);
    }
}
