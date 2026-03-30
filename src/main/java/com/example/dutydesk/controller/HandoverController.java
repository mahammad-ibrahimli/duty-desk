package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.handover.CreateHandoverRequest;
import com.example.dutydesk.dto.request.handover.UpdateHandoverRequest;
import com.example.dutydesk.dto.response.common.ApiResponse;
import com.example.dutydesk.dto.response.handover.CreateHandoverResponse;
import com.example.dutydesk.dto.response.handover.HandoverDetailResponse;
import com.example.dutydesk.dto.response.handover.HandoverListResponse;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.service.HandoverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RestController
@RequestMapping("/api/handovers")
@RequiredArgsConstructor
public class HandoverController {
    private final HandoverService handoverService;

    @GetMapping
    public ApiResponse<HandoverListResponse> getHandovers(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String search) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(handoverService.getHandovers(userDetails.getUsername(), status, from, to, search));
    }

    @GetMapping("/{id}")
    public ApiResponse<HandoverDetailResponse> getHandoverById(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(handoverService.getHandoverById(userDetails.getUsername(), id));
    }

    @PostMapping
    public ApiResponse<CreateHandoverResponse> createHandover(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateHandoverRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(handoverService.createHandover(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<HandoverDetailResponse> updateHandover(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody UpdateHandoverRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(handoverService.updateHandover(userDetails.getUsername(), id, request));
    }
}
