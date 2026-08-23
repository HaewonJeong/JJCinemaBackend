package com.jjcompany.jjcinemabackend.service;

import com.jjcompany.jjcinemabackend.dto.request.GenreRequest;
import com.jjcompany.jjcinemabackend.dto.request.SignupRequest;
import com.jjcompany.jjcinemabackend.dto.response.GenreResponse;
import com.jjcompany.jjcinemabackend.dto.response.SignupResponse;
import com.jjcompany.jjcinemabackend.domain.Genre;
import com.jjcompany.jjcinemabackend.domain.Rating;
import com.jjcompany.jjcinemabackend.repository.GenreRepository;
import java.util.List;

import com.jjcompany.jjcinemabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        Genre genre = Genre.create(request.genreName());
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
