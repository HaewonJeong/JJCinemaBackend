package com.jjcompany.jjcinemabackend.movie.repository;

import com.jjcompany.jjcinemabackend.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
