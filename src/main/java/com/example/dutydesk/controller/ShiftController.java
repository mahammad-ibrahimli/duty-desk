package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.shift.AddShiftNoteRequest;
import com.example.dutydesk.dto.request.shift.CheckInRequest;
import com.example.dutydesk.dto.request.shift.CheckOutRequest;
import com.example.dutydesk.dto.request.shift.CreateShiftChangeRequest;
import com.example.dutydesk.dto.response.common.ApiResponse;
import com.example.dutydesk.dto.response.shift.CheckInOutResponse;
import com.example.dutydesk.dto.response.shift.CurrentShiftResponse;
import com.example.dutydesk.dto.response.shift.ShiftChangeRequestResponse;
import com.example.dutydesk.dto.response.shift.ShiftListResponse;
import com.example.dutydesk.dto.response.shift.ShiftNoteResponse;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping
    public ApiResponse<ShiftListResponse> getShifts(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.getShifts(userDetails.getUsername(), status, from, to, page, limit));
    }

    @GetMapping("/current")
    public ApiResponse<CurrentShiftResponse> current(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.getCurrentShift(userDetails.getUsername()));
    }

    @PostMapping("/check-in")
    public ApiResponse<CheckInOutResponse> checkIn(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckInRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.checkIn(userDetails.getUsername(), request));
    }

    @PostMapping("/check-out")
    public ApiResponse<CheckInOutResponse> checkOut(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckOutRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.checkOut(userDetails.getUsername(), request));
    }

    @PostMapping("/change-request")
    public ApiResponse<ShiftChangeRequestResponse> changeRequest(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateShiftChangeRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.createChangeRequest(userDetails.getUsername(), request));
    }

    @GetMapping("/{shiftId}/notes")
    public ApiResponse<ShiftNoteResponse> getShiftNotes(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID shiftId) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(shiftService.getShiftNotes(userDetails.getUsername(), shiftId));
    }

    @PostMapping("/{shiftId}/notes")
    public ApiResponse<Void> addShiftNote(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID shiftId,
            @Valid @RequestBody AddShiftNoteRequest request) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        shiftService.addShiftNote(userDetails.getUsername(), shiftId, request);
        return ApiResponse.success(null);
    }
}
