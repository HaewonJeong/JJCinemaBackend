package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.PaymentRequest;
import com.jjcompany.jjcinemabackend.dto.response.PaymentResponse;
import com.jjcompany.jjcinemabackend.global.response.ApiResponse;
import com.jjcompany.jjcinemabackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //모의 결제 (forceResult로 성공/실패 강제 지정 가능)
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @RequestBody PaymentRequest request,
            Authentication authentication
    ){
        PaymentResponse response = paymentService.pay(request, authentication.getName());
        String message = "SUCCESS".equals(response.status())
                ? "결제가 완료되었습니다."
                : "결제에 실패했습니다. 좌석이 해제되었습니다.";
            return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    //예매 건의 결제 내역 조회
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long bookingId,
            Authentication authentication
    ){
        PaymentResponse response = paymentService.getByBookingId(bookingId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
