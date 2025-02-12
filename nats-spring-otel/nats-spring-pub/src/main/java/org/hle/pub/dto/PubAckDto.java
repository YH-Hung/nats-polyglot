package org.hle.pub.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PubAckDto {
    private final String stream;
    private final long seq;
    private final String domain;
    private final boolean duplicate;
}
