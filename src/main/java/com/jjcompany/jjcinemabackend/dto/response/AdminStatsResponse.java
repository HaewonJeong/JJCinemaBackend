package com.jjcompany.jjcinemabackend.dto.response;

public record AdminStatsResponse(
        int todayBookingCount,
        int todayRevenue,
        int totalBookingCount,
        int totalSeatsSold,
        int totalRevenue
) {
}
