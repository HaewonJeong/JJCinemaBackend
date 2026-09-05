package com.jjcompany.jjcinemabackend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(
        name = "uk_seats_showtime_seat", columnNames = {"showtime_id", "seat_code"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_code", nullable = false, length = 5)
    private String seatCode;

    @Column(nullable = false)
    private boolean occupied;

    @Column(name = "held_at")
    private LocalDateTime heldAt; // null = 비어있거나 결제완료(영구 점유). 값 있으면 그 시각부터 임시선점 중.

    //낙관적 락 테스트
    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long version;

    public static Seat create(Long showtimeId, String seatCode) {
        return new Seat(null, showtimeId, seatCode, false, null, 0L);
    }

    // 지금 이 순간 진짜로 예매 가능한지 (5분 지난 HELD는 사실상 빈 자리로 침)
    public boolean isAvailable(long holdTimeoutMinutes) {
        if (!occupied) return true;
        if (heldAt == null) return false; // 결제완료로 영구 점유
        return heldAt.plusMinutes(holdTimeoutMinutes).isBefore(LocalDateTime.now());
    }

    public void hold() {
        this.occupied = true;
        this.heldAt = LocalDateTime.now();
    }

    public void confirm() {
        this.heldAt = null; // 만료 없는 영구 점유로 전환
    }

    public void release() {
        this.occupied = false;
        this.heldAt = null;
    }
}
