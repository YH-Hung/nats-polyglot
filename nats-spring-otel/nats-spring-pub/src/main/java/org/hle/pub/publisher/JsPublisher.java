package org.hle.pub.publisher;

import io.micrometer.tracing.Tracer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.hle.pub.config.GirlsStreamConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JsPublisher {
    private final Connection nc;
    private final GirlsStreamConfig streamConfig;

    private final Tracer tracer;

    public JsPublisher(Connection nc, GirlsStreamConfig streamConfig, Tracer tracer) {
        this.nc = nc;
        this.streamConfig = streamConfig;
        this.tracer = tracer;
    }

    public PublishAck publish(String nextSubject, String rawMessage) throws IOException, JetStreamApiException {
        JetStream js = nc.jetStream();
        log.info("Start to publish girls messages");

        var header = genTraceHeaders(tracer);

        var ack = js.publish(NatsMessage.builder()
                .subject("%s.%s".formatted(streamConfig.getSubjectBase(), nextSubject))
                .headers(header)
                .data(rawMessage.getBytes(StandardCharsets.UTF_8))
                .build());

        log.info("Girl message published");

        return ack;


    }

    private static Headers genTraceHeaders(Tracer tracer) {
        var headers = new Headers();
        var ctx = tracer.currentTraceContext().context();
        if (ctx != null) {
            headers.add("OTEL-TRACE-ID", ctx.traceId());
            headers.add("OTEL-SPAN-ID", ctx.spanId());
        }

        return headers;
    }
}
