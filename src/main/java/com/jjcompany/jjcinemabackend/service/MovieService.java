package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.Movie;
import com.jjcompany.jjcinemabackend.dto.request.MovieRequest;
import com.jjcompany.jjcinemabackend.dto.response.MovieResponse;
import com.jjcompany.jjcinemabackend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public MovieResponse create(MovieRequest request, String createBy){
        Movie movie = Movie.create(request.title(), request.genreId(), request.runtime(), request.ratingId(),
                request.director(), request.releaseDate(), request.posterBase64(), request.synopsis(),
                request.status(), createBy);
        return MovieResponse.from(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse update(Long movieId, MovieRequest request, String updatedBy) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalStateException("영화를 찾을 수 없습니다."));

        movie.update(request.title(), request.genreId(),request.runtime(), request.ratingId(),
        request.director(), request.releaseDate(), request.posterBase64(), request.synopsis(),
        request.status(), updatedBy);

        return MovieResponse.from(movie);
    }

    @Transactional
    public void delete(Long movieId){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(()-> new IllegalStateException("영화를 찾을 수 없습니다."));
        try {
            movieRepository.delete(movie);
            movieRepository.flush(); // 트랜잭션 끝날 때까지 미루지 않고 지금 바로 FK 위반을 확인
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "등록된 상영이 있어 삭제할 수 없습니다. 상영을 먼저 정리해주세요.");
        }
    }
}
