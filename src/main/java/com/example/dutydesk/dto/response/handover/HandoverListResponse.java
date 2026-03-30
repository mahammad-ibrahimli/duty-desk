package com.example.dutydesk.dto.response.handover;

import com.example.dutydesk.dto.response.common.PaginationMeta;
import java.util.List;
import java.util.UUID;

public record HandoverListResponse(List<HandoverItem> handovers, PaginationMeta pagination) {

    public record HandoverItem(UUID id,
            String shiftType,
            String date,
            String time,
            UserSummary fromUser,
            UserSummary toUser,
            String status,
            String summary) {
    }

    public record UserSummary(UUID id, String name, String phone) {
    }
}
