package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Showtime;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeResponse(
        Long showtimeId,
        Long movieId,
        String movieTitle,
        LocalDate date,
        LocalTime time,
        String theater,
        Integer price
) {
    public static ShowtimeResponse from(Showtime showtime, String movieTitle) {
        return new ShowtimeResponse(showtime.getShowtimeId(), showtime.getMovieId(), movieTitle,
                showtime.getDate(), showtime.getTime(), showtime.getTheater(), showtime.getPrice());
    }
}
