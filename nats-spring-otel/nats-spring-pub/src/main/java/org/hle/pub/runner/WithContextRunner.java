package org.hle.pub.runner;

import io.micrometer.tracing.Tracer;
import io.nats.client.JetStreamApiException;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.propagation.TextMapGetter;
import lombok.extern.slf4j.Slf4j;
import org.hle.pub.publisher.JsPublisher;
import org.hle.pub.repository.GirlsRatingRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nats.nc-publisher", name = "enable", havingValue = "Y")
public class WithContextRunner {

    private final JsPublisher publisher;
    private final GirlsRatingRepository girlsRatingRepository;

    private final Tracer tracer;
    private final OpenTelemetry openTelemetry;
    
    public WithContextRunner(JsPublisher publisher, GirlsRatingRepository girlsRatingRepository, Tracer tracer, OpenTelemetry openTelemetry) {
        this.publisher = publisher;
        this.girlsRatingRepository = girlsRatingRepository;
        this.tracer = tracer;
        this.openTelemetry = openTelemetry;
    }

    @Scheduled(fixedDelay = 60000)
    public void run() {
        girlsRatingRepository.findFirstBy()
                .ifPresentOrElse(girl -> {
                    String contextStr = girl.getTracingContext();
                    log.info("Try to restore context from {}", contextStr);

                    Properties properties = new Properties();
                    Reader reader = new StringReader(contextStr);
                    try {
                        properties.load(reader);
                    } catch (IOException e) {
                        log.error("Failed to load tracing context", e);
                    }

                    var parentContext = openTelemetry.getPropagators()
                            .getTextMapPropagator()
                            .extract(io.opentelemetry.context.Context.root(), properties, getter);

                    try (var scope = parentContext.makeCurrent()) {
                        var span = tracer.nextSpan().name("Get record from DB").start();
                        try (var ws = tracer.withSpan(span)) {
                            publisher.publish("top", "%s with rating %f".formatted(girl.getName(), girl.getRating()));
                            girlsRatingRepository.deleteById(girl.getId());
                        } catch (JetStreamApiException | IOException e) {
                            log.error("Failed to publish message", e);
                        } finally {
                            span.end();
                        }
                    }

                }, () -> log.info("No rating found, do nothing..."));

        log.info("Finish...");
    }

    private static final TextMapGetter<Properties> getter = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Properties properties) {
            return properties.stringPropertyNames();
        }

        @Override
        public String get(Properties properties, String key) {
            return properties.getProperty(key);
        }
    };
}
