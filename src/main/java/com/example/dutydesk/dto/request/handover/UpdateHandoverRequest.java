package com.example.dutydesk.dto.request.handover;

import java.util.UUID;

public record UpdateHandoverRequest(
        String incidents,
        String systemStatus,
        String pendingTasks,
        String nextShiftInfo,
        String additionalNotes,
        String status,
        UUID toUserId) {
}
