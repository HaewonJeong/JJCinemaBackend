package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long bookingId,
        Integer amount,
        String status,
        String method,
        LocalDateTime paidAt,
        String bookingStatus
) {
    public static PaymentResponse  from(Payment payment, String bookingStatus){
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getPaidAt(),
                bookingStatus
        );
    }
}
