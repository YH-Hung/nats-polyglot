package org.hle.pub.dto;

import io.micrometer.tracing.Span;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class MessageWithSpan {
    private String message;
    private String traceId;
    private String spanId;
}
