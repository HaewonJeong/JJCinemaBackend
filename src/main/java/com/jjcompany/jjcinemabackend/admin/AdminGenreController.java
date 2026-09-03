package com.jjcompany.jjcinemabackend.admin;

import com.jjcompany.jjcinemabackend.dto.request.GenreRequest;
import com.jjcompany.jjcinemabackend.dto.response.GenreResponse;
import com.jjcompany.jjcinemabackend.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {

    private final GenreService genreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreResponse create(@RequestBody GenreRequest request) {
        return genreService.create(request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        genreService.delete(id);
    }
}
