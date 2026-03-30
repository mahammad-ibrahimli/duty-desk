package com.example.dutydesk.dto.response.shift;

import java.util.UUID;

public record ShiftChangeRequestResponse(
    UUID requestId,
    String status
) {}
