package org.hle.natsspring.runner;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.hle.natsspring.config.prop.GirlsStreamConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nats.connection", name = "enable", havingValue = "Y")
public class StreamLogger implements CommandLineRunner {

    private final Connection nc;
    private final GirlsStreamConfig streamConfig;

    public StreamLogger(Connection nc, GirlsStreamConfig streamConfig) {
        this.nc = nc;
        this.streamConfig = streamConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        var streamName = streamConfig.getStreamName();
        var consumerName = streamConfig.getConsumerName();
        var jcManage = nc.jetStreamManagement();
        log.info("Stream info of {}: {}", streamName, jcManage.getStreamInfo(streamName));
        log.info("Consumer info of {}: {}", consumerName, jcManage.getConsumerInfo(streamName, consumerName));
    }
}
