package org.hle.pub.dto;

import lombok.Data;

@Data
public class PubRequest {
    private String nextSubject;
    private String msgPayload;
}
