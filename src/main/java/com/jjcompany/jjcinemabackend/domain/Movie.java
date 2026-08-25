package com.jjcompany.jjcinemabackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "movies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    @Column(nullable = false)
    private Integer runtime;

    @Column(name = "rating_id", nullable = false)
    private Long ratingId;

    @Column(length = 100)
    private String director;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "poster_base64", columnDefinition = "text")
    private String posterBase64;

    @Column(columnDefinition = "text")
    private String synopsis;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Movie create(String title, Long genreId, Integer runtime, Long ratingId, String director,
                                LocalDate releaseDate, String posterBase64, String synopsis, String status,
                                String createdBy) {
        return new Movie(null, title, genreId, runtime, ratingId, director, releaseDate, posterBase64, synopsis,
                status, createdBy, null, null, null);
    }

    public void update(String title, Long genreId, Integer runtime, Long ratingId, String director,
                       LocalDate releaseDate, String posterBase64, String synopsis, String status,
                       String updatedBy){
        this.title = title;
        this.genreId = genreId;
        this.runtime = runtime;
        this.ratingId = ratingId;
        this.director = director;
        this.releaseDate = releaseDate;
        this.posterBase64 = posterBase64;
        this.synopsis = synopsis;
        this.status = status;
        this.updatedBy = updatedBy;
    }

}
