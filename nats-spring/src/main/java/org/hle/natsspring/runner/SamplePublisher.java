package org.hle.natsspring.runner;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.hle.natsspring.config.prop.GirlsStreamConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nats.publisher", name = "enable", havingValue = "Y")
public class SamplePublisher implements CommandLineRunner {

    private final Connection nc;
    private final GirlsStreamConfig streamConfig;

    public SamplePublisher(Connection nc, GirlsStreamConfig streamConfig) {
        this.nc = nc;
        this.streamConfig = streamConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        // https://github.com/nats-io/nats.java?tab=readme-ov-file#basic-usage
        JetStream js = nc.jetStream();

        log.info("Start to publish girls messages");

        // Add unique Nats-Msg-Id (String) header for deduplication in PublishOptions
        js.publish("%s.top".formatted(streamConfig.getSubjectBase()), "wan @ %tc".formatted(System.currentTimeMillis()).getBytes());
        js.publish("%s.top".formatted(streamConfig.getSubjectBase()), "chi han @ %tc".formatted(System.currentTimeMillis()).getBytes());
        js.publish(NatsMessage.builder()
                .subject("%s.normal".formatted(streamConfig.getSubjectBase()))
                .data("pan chin @ %tc".formatted(System.currentTimeMillis()), StandardCharsets.UTF_8)
                .build());

        log.info("Girl message published");
    }
}
