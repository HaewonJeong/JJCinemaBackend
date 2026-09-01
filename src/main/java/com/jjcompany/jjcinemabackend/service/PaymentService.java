package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.Booking;
import com.jjcompany.jjcinemabackend.domain.BookingSeat;
import com.jjcompany.jjcinemabackend.domain.Payment;
import com.jjcompany.jjcinemabackend.domain.Seat;
import com.jjcompany.jjcinemabackend.dto.request.PaymentRequest;
import com.jjcompany.jjcinemabackend.dto.response.PaymentResponse;
import com.jjcompany.jjcinemabackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final String STATUS_HELD = "HELD";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String METHOD_MOCK = "MOCK";

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public PaymentResponse pay(PaymentRequest request, String email) {

        //로그인 한 유저를 확인하고, 결제하려는 예매 Booking을 조회한다. 없으면 404를 반환.
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new IllegalStateException("예매를 찾을 수 없습니다."));

        //이 예매가 진짜 내 예매인지
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 예매만 결제할 수 있습니다.");
        }
        //지금 이 예매가 결제 가능한 상태 인지 확인 한다.
        if (!STATUS_HELD.equals(booking.getStatus())) {
            throw new IllegalArgumentException("결제 가능한 상태가 아닙니다.");
        }
        if (booking.getHeldAt().plusMinutes(5).isBefore(LocalDateTime.now())){
            List<BookingSeat> expired = bookingSeatRepository.findByBookingId(booking.getBookingId());
            expired.forEach(bs ->
                    seatRepository.findForUpdate(booking.getShowtimeId(), bs.getSeatCode()).ifPresent(Seat::release));
            bookingSeatRepository.deleteAll(expired);
            booking.cancel();
            throw new IllegalArgumentException("좌석 임시선점 시간이 만료되었습니다. 좌석을 다시 선택해주세요.");
        }

        //결제 성공/실패 여부를 결정 한다.
        boolean succeeded = resolveResult(request.forceResult());

        Payment payment = paymentRepository.save(
                Payment.create(
                        booking.getBookingId(),
                        booking.getTotalPrice(),
                        succeeded ? STATUS_SUCCESS : STATUS_FAILED,
                        METHOD_MOCK,
                        succeeded ? LocalDateTime.now() : null));

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getBookingId());
        if (succeeded) {
            booking.confirm();
            bookingSeats.forEach(bs ->
                    seatRepository.findForUpdate(booking.getShowtimeId(), bs.getSeatCode()).ifPresent(Seat::confirm));
        } else {
            bookingSeats.forEach(bs ->
                    seatRepository.findForUpdate(booking.getShowtimeId(), bs.getSeatCode()).ifPresent(Seat::release));
            bookingSeatRepository.deleteAll(bookingSeats);
            booking.cancel();
        }

        return PaymentResponse.from(payment, booking.getStatus());
    }


    public PaymentResponse getByBookingId(Long bookingId, String email) {
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("예매를 찾을 수 없습니다."));
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 결제 내역만 조회할수 있습니다.");
        }
        Payment payment = paymentRepository.findBookingByBookingId(bookingId)
                .orElseThrow(() -> new IllegalStateException("결제 내역을 찾을 수 없습니다."));
        return PaymentResponse.from(payment, booking.getStatus());
    }

    private boolean resolveResult(String forceResult) {
        if (forceResult == null) {
            return Math.random() > 0.5; //실패 vs 성공 랜덤하게 테스트
        }
        if (STATUS_SUCCESS.equals(forceResult)) {
            return true;
        }
        if (STATUS_FAILED.equals(forceResult)) {
            return false;
        }
        throw new IllegalArgumentException("forceResult는 SUCCESS 또는 FAILED만 가능합니다.");
    }

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
    }
}


