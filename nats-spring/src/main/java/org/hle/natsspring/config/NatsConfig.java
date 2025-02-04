package org.hle.natsspring.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsConfig {

    @Value("${nats.server.url}")
    String natsUrl;

    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        // Spring will consider auto closable
        return Nats.connect(natsUrl);
    }
}
