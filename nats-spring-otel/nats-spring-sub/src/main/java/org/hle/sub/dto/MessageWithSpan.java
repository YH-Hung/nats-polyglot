package org.hle.sub.dto;

import io.micrometer.tracing.Span;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MessageWithSpan {
    private String message;
    private String traceId;
    private String spanId;
}
