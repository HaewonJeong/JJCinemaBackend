package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Booking;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse( //① 괄호 부분 - "이 데이터가 어떤 필드들로 구성되는지"
        Long bookingId,
        Long showtimeId,
        String status,
        List<String> seatCodes,
        Integer totalPrice,
        LocalDateTime heldAt
){ // ② 중괄호 부분 - "그 외에 이 타입에 추가로 넣고 싶은 것들"
    public static BookingResponse from(Booking booking, List<String> seatCodes){
        return new BookingResponse(
                booking.getBookingId(),
                booking.getShowtimeId(),
                booking.getStatus(),
                seatCodes,
                booking.getTotalPrice(),
                booking.getHeldAt()
        );
    }
}
