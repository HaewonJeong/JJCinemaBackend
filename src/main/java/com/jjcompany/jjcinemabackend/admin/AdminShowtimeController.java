package com.jjcompany.jjcinemabackend.admin;

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
@RequestMapping("/api/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;

    //상영회차 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(
            @RequestBody ShowtimeRequest request,
            Authentication authentication
    ){
        ShowtimeResponse response = showtimeService.create(request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("상영 회차가 등록되었습니다.", response));
    }

    //여러 회차 일괄 수정
    @PatchMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> updateBulk(
            @RequestBody ShowtimeBulkUpdateRequest request,
            Authentication authentication
    ){
        List<ShowtimeResponse> updated = showtimeService.updateBulk(
                request.showtimeIds(), request.theater(), request.price(), authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("상영 정보가 수정되었습니다.",updated));

    }

    //개별 상영 시간 수정
    @PatchMapping("/{showtimeId}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(
            @PathVariable Long showtimeId,
            @RequestBody ShowtimeUpdateRequest request,
            Authentication authentication
    ){
        ShowtimeResponse response = showtimeService.update(showtimeId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("상영 정보가 수정되었습니다.", response));
    }
}
