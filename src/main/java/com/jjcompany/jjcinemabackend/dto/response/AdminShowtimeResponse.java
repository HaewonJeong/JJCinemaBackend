package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Showtime;

import java.time.LocalDate;
import java.time.LocalTime;

public record AdminShowtimeResponse(
        Long showtimeId,
        Long movieId,
        String movieTitle,
        LocalDate date,
        LocalTime time,
        String theater,
        Integer price,
        int bookedSeats,
        int totalSeats
) {
    public static AdminShowtimeResponse from(Showtime showtime, String movieTitle, int bookedSeats, int totalSeats) {
        return new AdminShowtimeResponse(
                showtime.getShowtimeId(), showtime.getMovieId(), movieTitle,
                showtime.getDate(), showtime.getTime(), showtime.getTheater(), showtime.getPrice(),
                bookedSeats, totalSeats);
    }
}