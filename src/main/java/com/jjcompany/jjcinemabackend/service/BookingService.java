package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.*;
import com.jjcompany.jjcinemabackend.dto.request.BookingCreateRequest;
import com.jjcompany.jjcinemabackend.dto.response.BookingResponse;
import com.jjcompany.jjcinemabackend.dto.response.SeatResponse;
import com.jjcompany.jjcinemabackend.repository.BookingRepository;
import com.jjcompany.jjcinemabackend.repository.BookingSeatRepository;
import com.jjcompany.jjcinemabackend.repository.ShowtimeRepository;
import com.jjcompany.jjcinemabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    //상영 회차의 좌석 배치도 + 예매 가능 여부
    public List<SeatResponse> getSeatMap(Long showtimeId){
        getShowtime(showtimeId); //존재 확인

        //Set은 순서가 없고 중복 제거되어, 좌석이 예매되어 있나를 확인할 때 사용
        Set<String> taken = bookingSeatRepository.findByShowtimeId(showtimeId).stream()
                .map(BookingSeat::getSeatCode)
                .collect(Collectors.toSet());

        return SeatLayout.allSeatCodes().stream()
                .map(code -> new SeatResponse(code, !taken.contains(code)))
                .toList();
    }

    //좌석 선점 (HELD)
    @Transactional
    public BookingResponse hold(BookingCreateRequest request, String email){
        Long userId = getUserId(email); //id 조회
        Showtime showtime = showtimeRepository.findByIdForUpdate(request.showtimeId())
                .orElseThrow( ()-> new IllegalStateException("상영 정보를 찾을 수 없습니다."));

        List<String> seatCodes = validateSeatCodes(request.seatCodes()); //좌석 코드 유효성 검사 및 조회

        int totalPrice = showtime.getPrice() * seatCodes.size(); //금액 계산
        Booking booking = bookingRepository.save(
                Booking.create(userId, showtime.getShowtimeId(), STATUS_HELD, totalPrice));
        try{
            for(String seatCode : seatCodes){
                bookingSeatRepository.save(
                        BookingSeat.create(booking.getBookingId(), showtime.getShowtimeId(), seatCode));
            }
            bookingSeatRepository.flush(); //여기서 UNIQUE 위반을 즉시 발생시킴
        }catch (DataIntegrityViolationException e){
            // booking_seats UNIQUE(showtime_id, seat_code) 위반 -> 다른 사용자가 먼저 선점
            // 트랜잭션 전체 롤백 (booking 도 취소됨)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 선점된 좌석이 있습니다.");
        }
        return BookingResponse.from(booking, seatCodes);
    }

    //예매 취소(좌석 해제)
    @Transactional
    public void cancel(Long bookingId, String email){
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new IllegalStateException("예매를 찾을 수 없습니다."));

        if (!booking.getUserId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 예매만 취소할 수 있습니다.");
        }
        if (STATUS_CANCELLED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("이미 취소된 예매입니다.");
        }

        bookingSeatRepository.deleteAll(bookingSeatRepository.findByBookingId(bookingId));
        booking.cancel();
        }

    //내 예매 목록
    public List<BookingResponse> getMyBookings(String email){
        Long userId = getUserId(email);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(booking -> BookingResponse.from(
                        booking,
                        bookingSeatRepository.findByBookingId(booking.getBookingId()).stream()
                                .map(BookingSeat::getSeatCode)
                                .toList()))
                .toList();
    }

    private List<String> validateSeatCodes(List<String> seatCodes){
        if(seatCodes == null || seatCodes.isEmpty()){
            throw new IllegalArgumentException("좌석을 하나 이상 선택해야 합니다.");
        }
        if (new HashSet<>(seatCodes).size() != seatCodes.size()){ //HashSet은 중복을 허용하지 않아, A1 A1 A3 과 같은 입력이 들어왔을때 거름
            throw new IllegalArgumentException("중복된 좌석이 있습니다.");
        }
        for (String code : seatCodes){
            if(!SeatLayout.isValid(code)){
                throw new IllegalArgumentException("존재하지 않는 좌석입니다.: "+ code);
            }
        }
        return seatCodes;
    }

    private Showtime getShowtime(Long showtimeId){
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(()-> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
    }

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getUserId())//user 하나를 받아서 그사람의 id를 꺼내라.
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
    }

    //임시 결제(상태만 HELD -> CONFIRMED)
    @Transactional
    public BookingResponse pay(Long bookingId, String email){
        Long userId = getUserId(email);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new IllegalStateException("예매를 찾을 수 없습니다."));

        if (!booking.getUserId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 예매만 결제할 수 있습니다.");
        }
        if(!STATUS_HELD.equals(booking.getStatus())){
            throw new IllegalArgumentException("결제 가능한 상태가 아닙니다.");
        }

        booking.confirm();

        List<String> seatCodes = bookingSeatRepository.findByBookingId(bookingId).stream()
                .map(BookingSeat::getSeatCode)
                .toList();
        return BookingResponse.from(booking, seatCodes);
        }

}
