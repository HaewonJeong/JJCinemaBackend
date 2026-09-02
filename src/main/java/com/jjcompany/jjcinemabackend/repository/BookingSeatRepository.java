package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByShowtimeId(Long showtimeId);
    List<BookingSeat> findByBookingId(Long bookingId);
    List<BookingSeat> findByBookingIdIn(List<Long> bookingIds);
    Optional<BookingSeat> findByShowtimeIdAndSeatCode(Long showtimeId, String seatCode);
}
