package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Genre;

public record GenreResponse(
        Long genreId,
        String genreName
) {
    public static GenreResponse from(Genre genre) {
        return new GenreResponse(genre.getGenreId(), genre.getGenreName());
    }
}
