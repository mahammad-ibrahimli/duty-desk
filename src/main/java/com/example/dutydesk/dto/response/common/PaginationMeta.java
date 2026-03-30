package com.example.dutydesk.dto.response.common;

public record PaginationMeta(int page, int limit, long total, int totalPages) {
}
