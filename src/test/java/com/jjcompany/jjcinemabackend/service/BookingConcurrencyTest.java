package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.*;
import com.jjcompany.jjcinemabackend.dto.request.BookingCreateRequest;
import com.jjcompany.jjcinemabackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingConcurrencyTest {

    @Autowired BookingService bookingService;
    @Autowired GenreRepository genreRepository;
    @Autowired RatingRepository ratingRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired ShowtimeRepository showtimeRepository;
    @Autowired SeatRepository seatRepository;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Long showtimeId;

    @BeforeEach
    void setUp() {
        Genre genre = genreRepository.save(Genre.create("테스트장르"));
        Rating rating = ratingRepository.save(Rating.create("전체"));
        Movie movie = movieRepository.save(Movie.create(
                "동시성테스트영화", genre.getGenreId(), 120, rating.getRatingId(),
                "감독", LocalDate.now(), null, "줄거리", "SHOWING", "test"
        ));
        Showtime showtime = showtimeRepository.save(Showtime.create(
                movie.getMovieId(), LocalDate.now(), LocalTime.of(20, 0), "1관", 14000, "test"
        ));
        showtimeId = showtime.getShowtimeId();
        for (String code : SeatLayout.allSeatCodes()) {
            seatRepository.save(Seat.create(showtimeId, code));
        }

        userRepository.save(User.create("userA@test.com", passwordEncoder.encode("pw12345!"), "userA"));
        userRepository.save(User.create("userB@test.com", passwordEncoder.encode("pw12345!"), "userB"));
    }

    @Test
    void 같은_좌석을_동시에_예매하면_한_명만_성공한다() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        String[] emails = {"userA@test.com", "userB@test.com"};

        for (String email : emails) {
            executor.submit(() -> {
                try {
                    bookingService.hold(new BookingCreateRequest(showtimeId, List.of("A1")), email);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // 핵심 검증: 둘 다 시도했지만 딱 한 명만 성공해야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}