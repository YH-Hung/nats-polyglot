package org.hle.sub.listener;

import io.micrometer.tracing.Tracer;
import io.nats.client.*;
import lombok.extern.slf4j.Slf4j;
import org.hle.sub.config.prop.GirlsStreamConfig;
import org.hle.sub.util.NatsUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class LongRunListener implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {

    private final Connection nc;
    private final GirlsStreamConfig streamConfig;
    private final ThreadPoolTaskExecutor executor;
    private final Tracer tracer;

    private volatile boolean isCancelled = false;

    public LongRunListener(Connection nc, GirlsStreamConfig streamConfig, @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor executor, Tracer tracer) {
        this.nc = nc;
        this.streamConfig = streamConfig;
        this.executor = executor;
        this.tracer = tracer;
    }

    @Override
    public void run(String... args) throws Exception {
        // https://github.com/nats-io/nats.java?tab=readme-ov-file#basic-usage
        var jsm = nc.jetStreamManagement();

        PullSubscribeOptions options = NatsUtil.consolidatePullSubscribeOptions(jsm, streamConfig.getStreamName(), streamConfig.getConsumerName());
        JetStream js = nc.jetStream();
        JetStreamSubscription sub = js.subscribe(null, options);

        executor.execute(() -> {
            while (!isCancelled) {
                try {
                    iterate(sub, Duration.ofSeconds(5), 5);
                } catch (Exception e) {
                    log.error("Error message", e);
                }
            }

            sub.unsubscribe();
            log.info("Polling cancelled.");
        });
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        isCancelled = true;
        log.warn("LongRunListener has been closed");
    }


    /**
     * According to jnats document, use fetch / iterate / reader
     * instead of pull / pullNoWait / pullExpireIn (primitive API).
     * fetch / iterate / reader are higher level API and were implemented by pullExpireIn or pull under the cover.
     * @param sub subscription option
     * @param pullTimeout timeout / duration for each pull batch
     * @param batchSize size per batch
     */
    private void iterate(JetStreamSubscription sub, Duration pullTimeout, int batchSize) {
        log.trace("Start new polling batch");
        var iter = sub.iterate(batchSize, pullTimeout);
        while (iter.hasNext()) {
            Message msg = iter.next();
            try {
                var headers = msg.getHeaders();
                var ctx = tracer.traceContextBuilder()
                        .traceId(headers.get("OTEL-TRACE-ID").get(0))
                        .spanId(headers.get("OTEL-SPAN-ID").get(0))
                        .build();

                var span = tracer.spanBuilder().setParent(ctx).name("nats polling").start();

                try (var ws = tracer.withSpan(span)) {
                    String payload = new String(msg.getData(), StandardCharsets.UTF_8);
                    log.info("Get message from subscribe stream: {}", payload);

                    // Use ackSync if you want to ensure server is received the ack. (by throw timeout exception)
                    msg.ack();
                } finally {
                    span.end();
                }

            } catch (Exception e) {
                log.error("Error message", e);
                msg.ack();
            }


        }

        log.trace("Polling batch complete.");
    }


}
