package com.jjcompany.jjcinemabackend.dto.request;

import java.util.List;

public record ShowtimeBulkUpdateRequest(
        List<Long> showtimeIds,
        String theater,
        Integer price
) {}
