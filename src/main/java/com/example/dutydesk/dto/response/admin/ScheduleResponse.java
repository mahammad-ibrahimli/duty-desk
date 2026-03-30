package com.example.dutydesk.dto.response.admin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ScheduleResponse(
        String week,
        List<ScheduleDay> days) {

    public record ScheduleDay(
            String date,
            String dayName,
            Map<String, List<UserShiftItem>> shifts) {
    }

    public record UserShiftItem(
            UUID userId,
            String userName) {
    }
}
