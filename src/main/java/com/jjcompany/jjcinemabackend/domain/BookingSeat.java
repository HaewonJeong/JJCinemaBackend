package com.jjcompany.jjcinemabackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_booking_seats_showtime_seat",
                columnNames = {"showtime_id", "seat_code"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_seat_id")
    private Long bookingSeatId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_code", nullable = false, length = 5)
    private String seatCode;

    public static BookingSeat create(Long bookingId, Long showtimeId, String seatCode) {
        return new BookingSeat(null, bookingId, showtimeId, seatCode);
    }
}
