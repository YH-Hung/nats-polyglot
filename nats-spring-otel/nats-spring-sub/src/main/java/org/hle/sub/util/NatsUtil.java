package org.hle.sub.util;

import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.PullSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.ConsumerInfo;
import io.nats.client.api.DeliverPolicy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class NatsUtil {
    public static Optional<ConsumerInfo> getConsumerInfo(JetStreamManagement jsm, String streamName, String consumerName) throws IOException, JetStreamApiException {
        try {
            return Optional.ofNullable(jsm.getConsumerInfo(streamName, consumerName));
        } catch (JetStreamApiException jsae) {
            if (jsae.getErrorCode() == 404) {
                return Optional.empty();
            }
            else {
                throw jsae;
            }
        }
    }

    public static boolean isConsumerExisted(JetStreamManagement jsm, String streamName, String consumerName) throws IOException, JetStreamApiException {
        return getConsumerInfo(jsm, streamName, consumerName).isPresent();
    }

    public static PullSubscribeOptions consolidatePullSubscribeOptions(JetStreamManagement jsm, String streamName, String consumerName) throws IOException, JetStreamApiException  {
        if (isConsumerExisted(jsm, streamName, consumerName)) {
            log.info("Consumer {} already existed, bind to it.", consumerName);
            return PullSubscribeOptions.builder()
                    .stream(streamName)
                    .durable(consumerName)
                    .build();
        } else {
            log.info("Consumer {} not existed yet, create one.", consumerName);
            ConsumerConfiguration cc = ConsumerConfiguration.builder()
                    .ackPolicy(AckPolicy.Explicit)
                    .filterSubject("%s.>".formatted(streamName))
                    .durable(consumerName)
                    .deliverPolicy(DeliverPolicy.All) // TODO: Change to ByStartSequence
                    .build();

            log.info("Consumer configuration: {}", cc);

            return PullSubscribeOptions.builder()
                    .stream(streamName)
                    .configuration(cc)
                    .build();
        }
    }
}
