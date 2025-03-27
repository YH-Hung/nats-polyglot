package org.hle.pub.controller;

import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Properties;

import org.hle.pub.dto.GirlRatingDto;
import org.hle.pub.entity.GirlsRating;
import org.hle.pub.repository.GirlsRatingRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/girls")
public class GirlController {

    private final GirlsRatingRepository girlsRatingRepository;
    private final Tracer tracer;
    private final OpenTelemetry openTelemetry;

    public GirlController(GirlsRatingRepository girlsRatingRepository, Tracer tracer, OpenTelemetry openTelemetry) {
        this.girlsRatingRepository = girlsRatingRepository;
        this.tracer = tracer;
        this.openTelemetry = openTelemetry;
    }

    @PostMapping("/rating")
    public GirlsRating addRating(@RequestBody GirlRatingDto rating) {
        var entity = new GirlsRating();


        var span = tracer.currentSpan();
        var properties = new Properties();
        openTelemetry.getPropagators().getTextMapPropagator().inject(
                io.opentelemetry.context.Context.current(), properties, setter
        );

        var writer = new StringWriter();
        try {
            properties.store(writer, null);
        } catch (IOException e) {
            log.error("Failed to store tracing context", e);
        }
        entity.setTracingContext(writer.toString());
        
        entity.setGirlId(rating.getGirlId());
        entity.setName(rating.getName());
        entity.setRating(BigDecimal.valueOf(rating.getRating()));

        log.info("Girl rating added...");
        return girlsRatingRepository.save(entity);
    }

    private static final TextMapSetter<Properties> setter = (properties, key, value) -> {
        if (properties != null && value != null) {
            properties.put(key, value);
        }
    };
}
