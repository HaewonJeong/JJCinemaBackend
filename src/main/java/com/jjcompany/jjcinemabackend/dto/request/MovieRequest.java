package com.jjcompany.jjcinemabackend.dto.request;

import java.time.LocalDate;

public record MovieRequest(
        String title,
        Long genreId,
        Integer runtime,
        Long ratingId,
        String director,
        LocalDate releaseDate,
        String posterBase64,
        String synopsis,
        String status
) {
}
