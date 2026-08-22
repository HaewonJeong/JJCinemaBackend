package com.jjcompany.jjcinemabackend.movie.dto.response;

import com.jjcompany.jjcinemabackend.movie.entity.Rating;

public record RatingResponse(
        Long id,
        String name
) {
    public static RatingResponse from(Rating rating) {
        return new RatingResponse(rating.getId(), rating.getName());
    }
}
