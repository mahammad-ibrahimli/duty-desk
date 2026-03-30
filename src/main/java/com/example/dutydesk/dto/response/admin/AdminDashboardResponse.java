package com.example.dutydesk.dto.response.admin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminDashboardResponse(
        Overview overview,
        List<OnDutyItem> onDutyNow,
        List<AlertItem> recentAlerts,
        WeeklyStats weeklyStats) {

    public record Overview(
            long totalEmployees,
            long activeShifts,
            long pendingHandovers,
            long todayCheckins) {
    }

    public record OnDutyItem(
            UUID id,
            String name,
            String team,
            String shiftType,
            String checkInTime,
            String status) {
    }

    public record AlertItem(
            UUID id,
            String type,
            String message,
            Instant timestamp) {
    }

    public record WeeklyStats(
            List<String> labels,
            List<Integer> checkins,
            List<Integer> handovers) {
    }
}
