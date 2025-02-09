package org.hle.natsspring.listener;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.hle.natsspring.config.prop.GirlsStreamConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimplifiedListener implements CommandLineRunner {
    private final Connection nc;
    private final GirlsStreamConfig streamConfig;

    public SimplifiedListener(Connection nc, GirlsStreamConfig streamConfig) {
        this.nc = nc;
        this.streamConfig = streamConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO: if consumer not existed, create one
        // TODO: get consumer context from jetStream
        // TODO: Create MessageConsumer
        // TODO: Stop message consumer when context closing
    }
}
