package com.example.dutydesk.dto.response.shift;

import com.example.dutydesk.dto.response.common.PaginationMeta;
import java.util.List;

public record ShiftListResponse(List<ShiftItemDto> shifts, PaginationMeta pagination) {
}
