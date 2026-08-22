package com.jjcompany.jjcinemabackend.movie.service;

import com.jjcompany.jjcinemabackend.movie.dto.request.GenreRequest;
import com.jjcompany.jjcinemabackend.movie.dto.response.GenreResponse;
import com.jjcompany.jjcinemabackend.movie.entity.Genre;
import com.jjcompany.jjcinemabackend.movie.repository.GenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional
    public GenreResponse create(GenreRequest request) {
        Genre genre = Genre.builder()
                .genresName(request.genresName())
                .build();
        return GenreResponse.from(genreRepository.save(genre));
    }

    public List<GenreResponse> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreResponse::from)
                .toList();
    }

    public GenreResponse findById(Long id) {
        return GenreResponse.from(getGenre(id));
    }

    @Transactional
    public void delete(Long id) {
        genreRepository.delete(getGenre(id));
    }

    private Genre getGenre(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found: " + id));
    }
}
