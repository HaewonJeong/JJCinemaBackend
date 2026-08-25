package com.jjcompany.jjcinemabackend.controller;

import com.jjcompany.jjcinemabackend.dto.request.ShowtimeBulkUpdateRequest;
import com.jjcompany.jjcinemabackend.dto.response.ShowtimeResponse;
import com.jjcompany.jjcinemabackend.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public List<ShowtimeResponse> getShowtimes(@RequestParam Long movieId){
        return showtimeService.getShowtimesByMovie(movieId);
    }

    @GetMapping("/{showtimeId}")
    public ShowtimeResponse getShowtime(@PathVariable Long showtimeId){
        return showtimeService.getShowtime(showtimeId);
    }

    @PatchMapping("/bulk")
    public List<ShowtimeResponse> updateBulk(@RequestBody ShowtimeBulkUpdateRequest request, Authentication authentication){
        return showtimeService.updateBulk(request.showtimeIds(), request.theater(), request.price(), authentication.getName());
    }

}
