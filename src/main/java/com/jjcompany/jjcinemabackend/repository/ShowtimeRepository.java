package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieIdOrderByDateAscTimeAsc(Long movieId);
}
