package com.jjcompany.jjcinemabackend.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeRequest(
        Long movieId,
        LocalDate date,
        LocalTime time,
        String theater,
        Integer price
) {
}
