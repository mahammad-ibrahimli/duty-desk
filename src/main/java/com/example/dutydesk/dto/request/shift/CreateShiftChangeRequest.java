package com.example.dutydesk.dto.request.shift;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateShiftChangeRequest(
    @NotNull UUID shiftId,
    @NotNull String reason,
    LocalDate requestedDate
) {}
