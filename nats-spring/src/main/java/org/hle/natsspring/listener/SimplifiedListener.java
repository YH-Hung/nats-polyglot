package org.hle.natsspring.listener;

import io.nats.client.Connection;
import io.nats.client.ConsumerContext;
import io.nats.client.JetStream;
import io.nats.client.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.hle.natsspring.config.prop.GirlsStreamConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nats.subscriber", name = "type", havingValue = "mc")
public class SimplifiedListener implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    private final Connection nc;
    private final GirlsStreamConfig streamConfig;
    private MessageConsumer mc;

    public SimplifiedListener(Connection nc, GirlsStreamConfig streamConfig) {
        this.nc = nc;
        this.streamConfig = streamConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        JetStream js = nc.jetStream();
        ConsumerContext consumerContext = js.getConsumerContext(streamConfig.getStreamName(), streamConfig.getConsumerName());

        // TODO: Test with Executor
        mc = consumerContext.consume(msg -> {
            String payload = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("Get message from subscribe stream: {}", payload);

            // Use ackSync if you want to ensure server is received the ack. (by throw timeout exception)
            msg.ack();
        });
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        mc.stop();
        log.warn("Message consumer stopped");
    }
}
