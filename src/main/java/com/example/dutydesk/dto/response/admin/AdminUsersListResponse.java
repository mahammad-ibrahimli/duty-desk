package com.example.dutydesk.dto.response.admin;

import com.example.dutydesk.dto.response.common.PaginationMeta;

import java.util.List;
import java.util.UUID;

public record AdminUsersListResponse(
                List<AdminUserItem> users,
                PaginationMeta pagination) {

        public record AdminUserItem(
                        UUID id,
                        String email,
                        String firstName,
                        String lastName,
                        String role,
                        boolean isActive,
                        TeamSummary team,
                        String phone) {
        }

        public record TeamSummary(
                        UUID id,
                        String name) {
        }
}
