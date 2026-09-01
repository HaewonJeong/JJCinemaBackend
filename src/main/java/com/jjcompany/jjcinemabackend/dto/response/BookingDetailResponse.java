package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Booking;
import com.jjcompany.jjcinemabackend.domain.Movie;
import com.jjcompany.jjcinemabackend.domain.Showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record BookingDetailResponse (
        Long bookingId,
        Long showtimeId,
        String status,
        List<String> seatCodes,
        Integer totalPrice,
        LocalDateTime heldAt,
        LocalDateTime holdExpiresAt,
        String movieTitle,
        String moviePosterBase64,
        LocalDate date,
        LocalTime time,
        String theater,
        String paymentStatus
) {
    public static BookingDetailResponse from(
            Booking booking, List<String> seatCodes, Movie movie, Showtime showtime,
            LocalDateTime holdExpiresAt, String paymentStatus) {
        return new BookingDetailResponse(
                booking.getBookingId(),
                booking.getShowtimeId(),
                booking.getStatus(),
                seatCodes,
                booking.getTotalPrice(),
                booking.getHeldAt(),
                holdExpiresAt,
                movie != null ? movie.getTitle() : "알 수 없음",
                movie != null ? movie.getPosterBase64() : null,
                showtime.getDate(),
                showtime.getTime(),
                showtime.getTheater(),
                paymentStatus
        );
    }
}
