package com.jjcompany.jjcinemabackend.movie.repository;

import com.jjcompany.jjcinemabackend.movie.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
