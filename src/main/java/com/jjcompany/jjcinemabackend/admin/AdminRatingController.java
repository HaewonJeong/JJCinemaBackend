package com.jjcompany.jjcinemabackend.admin;

import com.jjcompany.jjcinemabackend.dto.request.RatingRequest;
import com.jjcompany.jjcinemabackend.dto.response.RatingResponse;
import com.jjcompany.jjcinemabackend.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ratings")
@RequiredArgsConstructor
public class AdminRatingController {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse create(@RequestBody RatingRequest request) {
        return ratingService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ratingService.delete(id);
    }
}
