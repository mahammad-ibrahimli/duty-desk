package com.example.dutydesk.dto.response.handover;

import java.time.Instant;
import java.util.UUID;

public record HandoverDetailResponse(UUID id,
        ShiftInfo shift,
        UserInfo fromUser,
        UserInfo toUser,
        String incidents,
        String systemStatus,
        String pendingTasks,
        String nextShiftInfo,
        String additionalNotes,
        String status,
        Instant submittedAt,
        Instant approvedAt,
        UserInfo approvedBy) {

    public record ShiftInfo(UUID id, String type, String date, String startTime, String endTime) {
    }

    public record UserInfo(UUID id, String name, String team, String phone) {
    }
}
