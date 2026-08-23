package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.response.MovieResponse;
import com.jjcompany.jjcinemabackend.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getMovies(){
        return ResponseEntity.ok(movieService.getMovies());
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieResponse> getMovie(
            @PathVariable Long movieId
    ){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }
}
