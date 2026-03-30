package com.example.dutydesk.dto.request.handover;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateHandoverRequest(
        @NotNull(message = "shiftId is required") UUID shiftId,

        @NotBlank(message = "incidents is required") String incidents,

        @NotBlank(message = "systemStatus is required") String systemStatus,

        String pendingTasks,

        @NotBlank(message = "nextShiftInfo is required") String nextShiftInfo,

        String additionalNotes,
        String status,
        UUID toUserId) {
}
