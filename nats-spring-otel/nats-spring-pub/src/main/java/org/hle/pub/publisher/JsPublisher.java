package org.hle.pub.publisher;

import io.micrometer.tracing.Tracer;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.propagation.TextMapSetter;
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
    private final OpenTelemetry openTelemetry;

    public JsPublisher(Connection nc, GirlsStreamConfig streamConfig, Tracer tracer, OpenTelemetry openTelemetry) {
        this.nc = nc;
        this.streamConfig = streamConfig;
        this.tracer = tracer;
        this.openTelemetry = openTelemetry;
    }

    public PublishAck publish(String nextSubject, String rawMessage) throws IOException, JetStreamApiException {
        JetStream js = nc.jetStream();

        var span = tracer.currentSpan();    // or nextSpan -> try with span if current active context is empty
        var headers = new Headers();

        String subject = "%s.%s".formatted(streamConfig.getSubjectBase(), nextSubject);
        if (span != null) {
            openTelemetry.getPropagators().getTextMapPropagator().inject(
                    io.opentelemetry.context.Context.current(), headers, setter
            );

            return publish(js, subject, headers, rawMessage);
        } else {
            var newSpan = tracer.nextSpan().name("nats-publish").start();
            try (var ws = tracer.withSpan(newSpan)) {
                openTelemetry.getPropagators().getTextMapPropagator().inject(
                        io.opentelemetry.context.Context.current(), headers, setter
                );

                return publish(js, subject, headers, rawMessage);
            } finally {
                newSpan.end();
            }
        }

    }

    private static PublishAck publish(JetStream js, String subject, Headers headers, String rawMessage) throws JetStreamApiException, IOException {
        log.info("Start to publish girls messages");
        var ack = js.publish(NatsMessage.builder()
                .subject(subject)
                .headers(headers)
                .data(rawMessage.getBytes(StandardCharsets.UTF_8))
                .build());

        log.info("Girl message published");

        return ack;
    }

    private static final TextMapSetter<Headers> setter = (headers, key, value) -> {
        if (headers != null && value != null) {
            headers.put(key, value);
        }
    };
}
