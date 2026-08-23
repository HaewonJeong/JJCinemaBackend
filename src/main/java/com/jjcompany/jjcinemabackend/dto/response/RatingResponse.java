package com.jjcompany.jjcinemabackend.dto.response;

import com.jjcompany.jjcinemabackend.domain.Rating;

public record RatingResponse(
        Long ratingId,
        String name
) {
    public static RatingResponse from(Rating rating) {
        return new RatingResponse(rating.getRatingId(), rating.getName());
    }
}
