package com.example.dutydesk.dto.response.shift;

import java.util.List;

public record ShiftNoteResponse(
    List<ShiftNoteItem> notes
) {}
