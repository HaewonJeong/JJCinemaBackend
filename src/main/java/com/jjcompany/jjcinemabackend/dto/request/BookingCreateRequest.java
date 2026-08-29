package com.jjcompany.jjcinemabackend.dto.request;

import java.util.List;

public record BookingCreateRequest(
        Long showtimeId,
        List<String> seatCodes
) {
}
