package org.hle.pub.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.hle.pub.config.GirlsStreamConfig;
import org.hle.pub.dto.MessageWithSpan;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JsPublisher {
    private final Connection nc;
    private final GirlsStreamConfig streamConfig;

    private final Tracer tracer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsPublisher(Connection nc, GirlsStreamConfig streamConfig, Tracer tracer) {
        this.nc = nc;
        this.streamConfig = streamConfig;
        this.tracer = tracer;
    }

    public PublishAck publish(String nextSubject, String rawMessage) throws IOException, JetStreamApiException {
        JetStream js = nc.jetStream();
        log.info("Start to publish girls messages");

        var ctx = tracer.currentTraceContext().context();
        var msgWithSpan = MessageWithSpan.builder()
                .message(rawMessage)
                .traceId(ctx.traceId())
                .spanId(ctx.spanId())
                .build();

        var ack = js.publish(NatsMessage.builder()
                .subject("%s.%s".formatted(streamConfig.getSubjectBase(), nextSubject))
                .data(objectMapper.writeValueAsBytes(msgWithSpan))
                .build());

        log.info("Girl message published");

        return ack;


    }
}
