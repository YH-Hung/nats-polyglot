package org.hle.pub.repository;

import org.hle.pub.entity.GirlsRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GirlsRatingRepository extends JpaRepository<GirlsRating, Integer> {
    Optional<GirlsRating> findFirstBy();
}