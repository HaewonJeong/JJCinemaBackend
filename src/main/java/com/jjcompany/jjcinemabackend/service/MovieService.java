package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.Movie;
import com.jjcompany.jjcinemabackend.dto.response.MovieResponse;
import com.jjcompany.jjcinemabackend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieResponse> getMovies(){
        return movieRepository.findAll().stream()
                .map(movie -> {
                    return MovieResponse.from(movie);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long movieId){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalStateException("영화를 찾을 수 없습니다."));

        return MovieResponse.from(movie);
    }

}
