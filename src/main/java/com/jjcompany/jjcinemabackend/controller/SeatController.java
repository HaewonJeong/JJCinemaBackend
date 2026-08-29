package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.response.SeatResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes/{showtimeId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final BookingService bookingService;

    //상영 회차 좌석 배치도 + 예매 가능 여부 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatMap(
            @PathVariable Long showtimeId){
        return ResponseEntity.ok(ApiResponse.success(bookingService.getSeatMap(showtimeId)));
    }


}
