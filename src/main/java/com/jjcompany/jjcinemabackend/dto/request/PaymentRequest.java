package com.jjcompany.jjcinemabackend.dto.request;

public record PaymentRequest(
        Long bookingId,
        String forceResult
) {
}
