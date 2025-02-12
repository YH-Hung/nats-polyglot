package org.hle.pub.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "nats.girls")
public class GirlsStreamConfig {
    private String subjectBase;
}
