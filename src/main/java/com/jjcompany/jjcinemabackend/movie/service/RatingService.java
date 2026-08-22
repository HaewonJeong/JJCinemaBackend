package com.jjcompany.jjcinemabackend.movie.service;

import com.jjcompany.jjcinemabackend.movie.dto.request.RatingRequest;
import com.jjcompany.jjcinemabackend.movie.dto.response.RatingResponse;
import com.jjcompany.jjcinemabackend.movie.entity.Rating;
import com.jjcompany.jjcinemabackend.movie.repository.RatingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;

    @Transactional
    public RatingResponse create(RatingRequest request) {
        Rating rating = Rating.builder()
                .name(request.name())
                .build();
        return RatingResponse.from(ratingRepository.save(rating));
    }

    public List<RatingResponse> findAll() {
        return ratingRepository.findAll().stream()
                .map(RatingResponse::from)
                .toList();
    }

    public RatingResponse findById(Long id) {
        return RatingResponse.from(getRating(id));
    }

    @Transactional
    public void delete(Long id) {
        ratingRepository.delete(getRating(id));
    }

    private Rating getRating(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found: " + id));
    }
}
