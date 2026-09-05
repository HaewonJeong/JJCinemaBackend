package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.domain.Movie;
import com.jjcompany.jjcinemabackend.domain.Seat;
import com.jjcompany.jjcinemabackend.domain.SeatLayout;
import com.jjcompany.jjcinemabackend.domain.Showtime;
import com.jjcompany.jjcinemabackend.dto.request.ShowtimeRequest;
import com.jjcompany.jjcinemabackend.dto.request.ShowtimeUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.response.ShowtimeResponse;
import com.jjcompany.jjcinemabackend.repository.MovieRepository;
import com.jjcompany.jjcinemabackend.repository.SeatRepository;
import com.jjcompany.jjcinemabackend.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getShowtimesByMovie(Long movieId){
        String movieTitle = getMovieTitle(movieId);
        return showtimeRepository.findByMovieIdOrderByDateAscTimeAsc(movieId).stream()
                .map(showtime -> ShowtimeResponse.from(showtime, movieTitle))
                .toList();
    }

    @Transactional(readOnly = true)
    public ShowtimeResponse getShowtime(Long showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow( () -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
        return ShowtimeResponse.from(showtime, getMovieTitle(showtime.getMovieId()));
    }

    @Transactional
    public List<ShowtimeResponse> updateBulk(List<Long> showtimeIds, String theater, Integer price, String updatedBy){
        List<Showtime> showtimes = showtimeRepository.findAllById(showtimeIds);
        showtimes.forEach(showtime -> showtime.update(null, null, theater, price, updatedBy));
        return showtimes.stream()
                .map(showtime -> ShowtimeResponse.from(showtime, getMovieTitle(showtime.getMovieId())))
                .toList();
    }

    private String getMovieTitle(Long movieId){
        return movieRepository.findById(movieId).map(Movie::getTitle).orElse("알 수 없음");
    }

    @Transactional
    public ShowtimeResponse create(ShowtimeRequest request, String createdBy) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(()-> new IllegalStateException("영화를 찾을 수 없습니다."));

        Showtime showtime = Showtime.create(
                request.movieId(), request.date(), request.time(),
                request.theater(), request.price(), createdBy
        );
        showtimeRepository.save(showtime);
        for (String seatCode : SeatLayout.allSeatCodes()) {
            seatRepository.save(Seat.create(showtime.getShowtimeId(), seatCode));
        }
        return ShowtimeResponse.from(showtime, movie.getTitle());
    }

    @Transactional
    public ShowtimeResponse update(Long showtimeId, ShowtimeUpdateRequest request, String updatedBy) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
        showtime.update(request.date(), request.time(), request.theater(), request.price(), updatedBy);
        return ShowtimeResponse.from(showtime, getMovieTitle(showtime.getMovieId()));
    }

    @Transactional
    public void delete(Long showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalStateException("상영 정보를 찾을 수 없습니다."));
        seatRepository.deleteByShowtimeId(showtimeId); // seats는 이 상영만 참조하므로 항상 안전하게 먼저 삭제 가능
        try {
            showtimeRepository.delete(showtime);
            showtimeRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "예매 내역이 있어 상영을 삭제할 수 없습니다. 예매를 먼저 정리해주세요.");
        }
    }


}
