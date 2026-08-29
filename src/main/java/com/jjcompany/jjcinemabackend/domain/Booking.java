package com.jjcompany.jjcinemabackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "held_at", nullable = false, updatable = false)
    private LocalDateTime heldAt;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Booking create(Long userId, Long showtimeId, String status, Integer totalPrice) {
        return new Booking(null, userId, showtimeId, status, null, null, totalPrice, null, null);
    }

    public void cancel(){
        this.status = "CANCELLED";
    }

    public void confirm(){
        this.status = "CONFIRMED";
        this.bookedAt = LocalDateTime.now();
    }
}
