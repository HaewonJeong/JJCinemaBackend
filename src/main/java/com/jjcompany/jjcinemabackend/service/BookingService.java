package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.*;
import com.jjcompany.jjcinemabackend.dto.request.BookingCreateRequest;
import com.jjcompany.jjcinemabackend.dto.response.BookingDetailResponse;
import com.jjcompany.jjcinemabackend.dto.response.BookingResponse;
import com.jjcompany.jjcinemabackend.dto.response.SeatResponse;
import com.jjcompany.jjcinemabackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private static final String STATUS_HELD = "HELD";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final long HOLD_TIMEOUT_MINUTES = 5;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final MovieRepository movieRepository;

    //상영 회차의 좌석 배치도 + 예매 가능 여부
    public List<SeatResponse> getSeatMap(Long showtimeId) {
        getShowtime(showtimeId);
        return seatRepository.findByShowtimeId(showtimeId).stream()
                .map(seat -> new SeatResponse(seat.getSeatCode(), seat.isAvailable(HOLD_TIMEOUT_MINUTES)))
                .toList();
    }

    //좌석 선점 (HELD)
    @Transactional
    public BookingResponse hold(BookingCreateRequest request, String email) {
        Long userId = getUserId(email);
        Showtime showtime = showtimeRepository.findById(request.showtimeId())
                .orElseThrow(() -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));

        List<String> seatCodes = validateSeatCodes(request.seatCodes());
        List<String> sortedCodes = seatCodes.stream().sorted().toList(); // 데드락 방지: 항상 같은 순서로 락

        List<Seat> lockedSeats = new ArrayList<>();
        for (String code : sortedCodes) {
            Seat seat = seatRepository.findForUpdate(showtime.getShowtimeId(), code)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다: " + code));
            if (!seat.isAvailable(HOLD_TIMEOUT_MINUTES)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점되었거나 예약된 좌석입니다: " + code);
            }
            lockedSeats.add(seat);
        }
        lockedSeats.forEach(Seat::hold);

        int totalPrice = showtime.getPrice() * seatCodes.size();
        Booking booking = bookingRepository.save(
                Booking.create(userId, showtime.getShowtimeId(), STATUS_HELD, totalPrice));

        for (String seatCode : seatCodes) {
            bookingSeatRepository.save(
                    BookingSeat.create(booking.getBookingId(), showtime.getShowtimeId(), seatCode));
        }
        return BookingResponse.from(booking, seatCodes);
    }

    //예매 취소(좌석 해제)
    @Transactional
    public void cancel(Long bookingId, String email) {
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("예매를 찾을 수 없습니다."));

        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 예매만 취소할 수 있습니다.");
        }
        if (STATUS_CANCELLED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("이미 취소된 예매입니다.");
        }

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);
        bookingSeats.forEach(bs ->
                seatRepository.findForUpdate(booking.getShowtimeId(), bs.getSeatCode()).ifPresent(Seat::release));
        bookingSeatRepository.deleteAll(bookingSeats);
        booking.cancel();
    }

    //예매 1건 상세 조회 (결제 페이지용)
    public BookingDetailResponse getBookingDetail(Long bookingId, String email) {
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("예매를 찾을 수 없습니다."));
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 예매만 조회할 수 있습니다.");
        }
        return toDetailResponse(booking);
    }

        //내 예매 목록
        public List<BookingDetailResponse> getMyBookings(String email){
            Long userId = getUserId(email);
            return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                    .map(this::toDetailResponse)
                    .toList();
        }

        //예매 1건을 화면용 상세 응답으로 조립 (영화/상영/결제 정보까지 합침)
        private BookingDetailResponse toDetailResponse (Booking booking){
            List<String> seatCodes = bookingSeatRepository.findByBookingId(booking.getBookingId()).stream()
                    .map(BookingSeat::getSeatCode)
                    .toList();
            Showtime showtime = showtimeRepository.findById(booking.getShowtimeId())
                    .orElseThrow(() -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
            Movie movie = movieRepository.findById(showtime.getMovieId()).orElse(null);
            LocalDateTime holdExpiresAt = STATUS_HELD.equals(booking.getStatus())
                    ? booking.getHeldAt().plusMinutes(HOLD_TIMEOUT_MINUTES)
                    : null;
            String paymentStatus = paymentRepository.findBookingByBookingId(booking.getBookingId())
                    .map(Payment::getStatus)
                    .orElse(null);

            return BookingDetailResponse.from(booking, seatCodes, movie, showtime, holdExpiresAt, paymentStatus);
        }

        private List<String> validateSeatCodes (List < String > seatCodes) {
            if (seatCodes == null || seatCodes.isEmpty()) {
                throw new IllegalArgumentException("좌석을 하나 이상 선택해야 합니다.");
            }
            if (new HashSet<>(seatCodes).size() != seatCodes.size()) { //HashSet은 중복을 허용하지 않아, A1 A1 A3 과 같은 입력이 들어왔을때 거름
                throw new IllegalArgumentException("중복된 좌석이 있습니다.");
            }
            for (String code : seatCodes) {
                if (!SeatLayout.isValid(code)) {
                    throw new IllegalArgumentException("존재하지 않는 좌석입니다.: " + code);
                }
            }
            return seatCodes;
        }

        private Showtime getShowtime (Long showtimeId){
            return showtimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
        }

        private Long getUserId (String email){
            return userRepository.findByEmail(email)
                    .map(user -> user.getUserId())//user 하나를 받아서 그사람의 id를 꺼내라.
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
        }

    }
