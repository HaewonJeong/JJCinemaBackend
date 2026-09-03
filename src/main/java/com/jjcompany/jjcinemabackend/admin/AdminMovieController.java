package com.jjcompany.jjcinemabackend.admin;

import com.jjcompany.jjcinemabackend.dto.request.MovieRequest;
import com.jjcompany.jjcinemabackend.dto.response.MovieResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    //새 영화 등록
    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> create(
            @RequestBody MovieRequest request,
            Authentication authentication
    ){
        MovieResponse response = movieService.create(request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("영화가 등록되었습니다", response));
    }

    //영화 수정
    @PatchMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponse>> update(
            @PathVariable Long movieId,
            @RequestBody MovieRequest request,
            Authentication authentication
    ){
        MovieResponse response = movieService.update(movieId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("영화 정보가 수정되었습니다.", response));
    }
}
