package org.hle.natsspring;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.impl.NatsMessage;
import lombok.SneakyThrows;
import org.hle.natsspring.config.prop.GirlsStreamConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("resource")
@Testcontainers
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=nats"})
class NatsSpringApplicationTests {

    @Container
    static GenericContainer<?> nats = new GenericContainer<>(DockerImageName.parse("nats:latest"))
            .withExposedPorts(4222, 8222)
            .withCommand("-js", "-m", "8222")
            .waitingFor(Wait.forHttp("/healthz").forPort(8222).forStatusCode(200));


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("nats.server.url", () -> "nats://%s:%s".formatted(nats.getHost(), nats.getMappedPort(4222)));
    }

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        String hostUrl = "nats://%s:%s".formatted(nats.getHost(), nats.getMappedPort(4222));
        Connection nc = Nats.connect(hostUrl);
        var jsm = nc.jetStreamManagement();
        var sc = StreamConfiguration.builder()
                .name("my-girls")
                .subjects("girls.>")
                .build();

        jsm.addStream(sc);

        var cc = ConsumerConfiguration.builder()
                        .durable("girl_consumer")
                        .build();

        jsm.createConsumer("my-girls", cc);
    }

    @Autowired
    Connection nc;

    @Autowired
    GirlsStreamConfig streamConfig;

    @SneakyThrows
    @Test
    void contextLoads(CapturedOutput output) {
        var js = nc.jetStream();
        js.publish(NatsMessage.builder()
                .subject("%s.normal".formatted(streamConfig.getSubjectBase()))
                .data("Zi Lun @ %tc".formatted(System.currentTimeMillis()), StandardCharsets.UTF_8)
                .build());

        Awaitility.await().untilAsserted(() -> {
            assertThat(output).contains("Get message from subscribe stream: Zi Lun");
        });
    }


}
