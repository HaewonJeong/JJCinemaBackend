package com.jjcompany.jjcinemabackend.movie.dto.response;

import com.jjcompany.jjcinemabackend.movie.entity.Genre;

public record GenreResponse(
        Long genresId,
        String genresName
) {
    public static GenreResponse from(Genre genre) {
        return new GenreResponse(genre.getGenresId(), genre.getGenresName());
    }
}
