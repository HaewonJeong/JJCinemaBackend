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
        // 임시선점 만료까지 남은 초. 프론트가 절대시각 파싱(타임존 이슈) 대신 이 값으로 카운트다운한다.
        Long holdRemainingSeconds,
        String movieTitle,
        String moviePosterBase64,
        LocalDate date,
        LocalTime time,
        String theater,
        String paymentStatus
) {
    public static BookingDetailResponse from(
            Booking booking, List<String> seatCodes, Movie movie, Showtime showtime,
            LocalDateTime holdExpiresAt, Long holdRemainingSeconds, String paymentStatus) {
        return new BookingDetailResponse(
                booking.getBookingId(),
                booking.getShowtimeId(),
                booking.getStatus(),
                seatCodes,
                booking.getTotalPrice(),
                booking.getHeldAt(),
                holdExpiresAt,
                holdRemainingSeconds,
                movie != null ? movie.getTitle() : "알 수 없음",
                movie != null ? movie.getPosterBase64() : null,
                showtime.getDate(),
                showtime.getTime(),
                showtime.getTheater(),
                paymentStatus
        );
    }
}
