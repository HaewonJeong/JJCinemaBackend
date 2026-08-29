package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.RatingRequest;
import com.jjcompany.jjcinemabackend.dto.response.RatingResponse;
import com.jjcompany.jjcinemabackend.service.RatingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    //AI 코드. 아직 안읽어봄
    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse create(@RequestBody RatingRequest request) {
        return ratingService.create(request);
    }

    @GetMapping
    public List<RatingResponse> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public RatingResponse findById(@PathVariable Long id) {
        return ratingService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ratingService.delete(id);
    }
}
