package com.jjcompany.jjcinemabackend.repository;

import com.jjcompany.jjcinemabackend.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
