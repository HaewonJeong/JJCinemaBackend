package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.AdminUserUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.response.AdminShowtimeResponse;
import com.jjcompany.jjcinemabackend.dto.response.AdminStatsResponse;
import com.jjcompany.jjcinemabackend.dto.response.AdminUserResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStats()));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<ApiResponse<List<AdminShowtimeResponse>>> getShowtimes() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getShowtimes()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers()));
    }

    @PatchMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUsers(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequest request,
            Authentication authentication
    ) {
        AdminUserResponse response = adminService.updateUser(userId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다.", response));
    }
}
