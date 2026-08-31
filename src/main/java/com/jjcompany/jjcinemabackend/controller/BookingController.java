package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.BookingCreateRequest;
import com.jjcompany.jjcinemabackend.dto.response.BookingResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //좌석 선점(예매 생성)
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> hold(
            @RequestBody BookingCreateRequest request,
            Authentication authentication
    ){
        BookingResponse response = bookingService.hold(request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("좌석이 선점되었습니다.", response));
    }

    //내 예매 목록 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            Authentication authentication
    ){
        List<BookingResponse> bookings = bookingService.getMyBookings(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    //예매 취소
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long bookingId,
            Authentication authentication
    ){
        bookingService.cancel(bookingId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("예매가 취소되었습니다.", null));
    }

}
