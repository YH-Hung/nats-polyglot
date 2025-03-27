package org.hle.pub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "girls_rating")
public class GirlsRating {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "girls_rating_id_gen")
    @SequenceGenerator(name = "girls_rating_id_gen", sequenceName = "girls_rating_rating_id_seq", allocationSize = 1)
    @Column(name = "rating_id", nullable = false)
    private Integer id;

    @Column(name = "tracing_context", length = Integer.MAX_VALUE)
    private String tracingContext;

    @Column(name = "girl_id")
    private Short girlId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

}