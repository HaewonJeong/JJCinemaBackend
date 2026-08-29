package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.Showtime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Showtime s where s.showtimeId = :showtimeId")
    Optional<Showtime> findByIdForUpdate(Long showtimeId);

    List<Showtime> findByMovieIdOrderByDateAscTimeAsc(Long movieId);
}
