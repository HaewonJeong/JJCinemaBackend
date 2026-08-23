package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Movie;

import java.time.LocalDate;

public record MovieResponse (
        Long movieId,
        String title,
        Long genreID,
        Integer runtime,
        Long ratingId,
        String director,
        LocalDate releaseDate,
        String posterBase64,
        String synopsis,
        String status
) {
    public static MovieResponse from(Movie movie){
        return new MovieResponse(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getGenreId(),
                movie.getRuntime(),
                movie.getRatingId(),
                movie.getDirector(),
                movie.getReleaseDate(),
                movie.getPosterBase64(),
                movie.getSynopsis(),
                movie.getStatus()
        );
    }
}
