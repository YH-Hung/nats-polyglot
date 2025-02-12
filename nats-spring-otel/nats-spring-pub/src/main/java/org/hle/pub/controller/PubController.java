package org.hle.pub.controller;

import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;
import org.hle.pub.dto.PubAckDto;
import org.hle.pub.dto.PubRequest;
import org.hle.pub.publisher.JsPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/pub")
public class PubController {

    private final JsPublisher publisher;

    public PubController(JsPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<PubAckDto> publish(@RequestBody PubRequest pubRequest) {
        try {
            PublishAck pubAck = publisher.publish(pubRequest.getNextSubject(), pubRequest.getMsgPayload());
            var dto = PubAckDto.builder()
                    .domain(pubAck.getDomain())
                    .stream(pubAck.getStream())
                    .seq(pubAck.getSeqno())
                    .duplicate(pubAck.isDuplicate())
                    .build();

            return ResponseEntity.ok(dto);
        } catch (IOException | JetStreamApiException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
