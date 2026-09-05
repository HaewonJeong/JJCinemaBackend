package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowtimeId(Long showtimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.showtimeId = :showtimeId and s.seatCode = :seatCode")
    Optional<Seat> findForUpdate(Long showtimeId, String seatCode);
    void deleteByShowtimeId(Long showtimeId);

}
