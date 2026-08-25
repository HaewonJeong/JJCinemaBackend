package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.MovieRequest;
import com.jjcompany.jjcinemabackend.dto.response.MovieResponse;
import com.jjcompany.jjcinemabackend.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@RequestBody MovieRequest request, Authentication authentication){
        return movieService.create(request, authentication.getName());
    }
}
