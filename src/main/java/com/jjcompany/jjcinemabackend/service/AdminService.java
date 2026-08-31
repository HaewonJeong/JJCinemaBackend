package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.*;
import com.jjcompany.jjcinemabackend.dto.request.AdminUserUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.response.AdminShowtimeResponse;
import com.jjcompany.jjcinemabackend.dto.response.AdminStatsResponse;
import com.jjcompany.jjcinemabackend.dto.response.AdminUserResponse;
import com.jjcompany.jjcinemabackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final int TOTAL_SEATS = 40;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    //대시보드 통계(결제 완료된 예매 기준)
    public AdminStatsResponse getStats(){
        List<Booking> confirmed = bookingRepository.findByStatus(STATUS_CONFIRMED);
        List<Booking> todayConfirmed = confirmed.stream()
                .filter(b-> b.getBookedAt() != null && b.getBookedAt().toLocalDate().equals(LocalDate.now()))
                .toList();

        int totalSeatsSold = countSeats(confirmed);
        int todayRevenue = todayConfirmed.stream().mapToInt(Booking::getTotalPrice).sum();
        int totalRevenue = confirmed.stream().mapToInt(Booking::getTotalPrice).sum();

        return new AdminStatsResponse(
                todayConfirmed.size(), todayRevenue, confirmed.size(), totalSeatsSold, totalRevenue);
    }

    private int countSeats(List<Booking> bookings){
        List<Long> bookingIds = bookings.stream().map(Booking::getBookingId).toList();
        if (bookingIds.isEmpty()) {
            return 0;
        }
        return bookingSeatRepository.findByBookingIdIn(bookingIds).size();  // findByBookingId → findByBookingIdIn
    }

    //상영 회차별 예매 현황(일괄수정/점유율 화면용)
    public List<AdminShowtimeResponse> getShowtimes(){
        List<Showtime> showtimes = showtimeRepository.findAll();
        Map<Long, Long> bookedByShowtime = bookingSeatRepository.findAll().stream()
                .collect(Collectors.groupingBy(BookingSeat::getShowtimeId, Collectors.counting()));

        return showtimes.stream()
                .map(showtime -> {
                    String movieTitle = movieRepository.findById(showtime.getMovieId())
                            .map(Movie::getTitle).orElse("알 수 없음");
                    int bookSeats = bookedByShowtime.getOrDefault(showtime.getShowtimeId(), 0L).intValue();
                    return AdminShowtimeResponse.from(showtime, movieTitle, bookSeats, TOTAL_SEATS);
                })
                .toList();
    }

    //회원 목록
    public List<AdminUserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    //회원 role/active 수정 (본인 계정은 변경 불가)
    @Transactional
    public AdminUserResponse updateUser(Long userId, AdminUserUpdateRequest request, String adminEmail){
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
        if (admin.getUserId().equals(userId)){
            throw new IllegalArgumentException("본인 계정은 변경할 수 없습니다.");
        }

        User target = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalStateException("회원을 찾을 수 없습니다."));
        target.updateByAdmin(request.role(), request.active(), adminEmail);
        return AdminUserResponse.from(target);
    }
}
