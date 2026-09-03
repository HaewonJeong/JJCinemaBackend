package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.ShowtimeBulkUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.request.ShowtimeRequest;
import com.jjcompany.jjcinemabackend.dto.request.ShowtimeUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.response.ShowtimeResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    //특정 영화의 상영 회차 목록 조회,  /api/showtimes?movieId=
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getShowtimes(
            @RequestParam Long movieId){
        List<ShowtimeResponse> showtimes = showtimeService.getShowtimesByMovie(movieId);
        return ResponseEntity.ok(ApiResponse.success(showtimes));
    }

    //상영 회차 하나 조회
    @GetMapping("/{showtimeId}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getShowtime(
            @PathVariable Long showtimeId){
        ShowtimeResponse showtime = showtimeService.getShowtime(showtimeId);
        return ResponseEntity.ok(ApiResponse.success(showtime));
    }

}
