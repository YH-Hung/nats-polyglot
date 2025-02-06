package org.hle.natsspring.listener;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamSubscription;
import io.nats.client.PullSubscribeOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LongRunListener implements CommandLineRunner {

    private final Connection nc;
    private final ThreadPoolTaskExecutor executor;

    public LongRunListener(Connection nc, @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor executor) {
        this.nc = nc;
        this.executor = executor;
    }

    @Override
    public void run(String... args) throws Exception {
        // https://github.com/nats-io/nats.java?tab=readme-ov-file#basic-usage
        PullSubscribeOptions options = PullSubscribeOptions.builder()
                .stream("my-girls")
                .durable("girl_consumer")
                .build();

        JetStream js = nc.jetStream();
        JetStreamSubscription sub = js.subscribe(null, options);

        // TODO: Fetch demo
        // TODO: Iterate demo
        // TODO: PullNoWait demo
        // TODO: PullExpire demo
    }
}
