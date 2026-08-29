package com.jjcompany.jjcinemabackend.dto.response;

public record SeatResponse(
        String seatCode,
        boolean available
) {
}
