package org.hle.natsspring.config.prop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "nats.girls")
public class GirlsStreamConfig {
    private String streamName;
    private String consumerName;
    private String subjectBase;
}
