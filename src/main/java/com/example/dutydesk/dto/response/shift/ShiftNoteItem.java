package com.example.dutydesk.dto.response.shift;

import java.time.Instant;
import java.util.UUID;

public record ShiftNoteItem(
    UUID id,
    String content,
    Instant createdAt
) {}
