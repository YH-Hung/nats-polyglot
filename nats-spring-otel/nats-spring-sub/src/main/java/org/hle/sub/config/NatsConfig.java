package org.hle.sub.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
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
        Options o = new Options.Builder()
                .server(natsUrl)
                .maxReconnects(-1)
                .build();

        // Spring will consider auto closable
        return Nats.connect(o);
    }
}