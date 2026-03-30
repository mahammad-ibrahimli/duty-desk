package com.example.dutydesk.service.impl;

import com.example.dutydesk.dto.request.handover.CreateHandoverRequest;
import com.example.dutydesk.dto.request.handover.UpdateHandoverRequest;
import com.example.dutydesk.dto.response.common.PaginationMeta;
import com.example.dutydesk.dto.response.handover.CreateHandoverResponse;
import com.example.dutydesk.dto.response.handover.HandoverDetailResponse;
import com.example.dutydesk.dto.response.handover.HandoverListResponse;
import com.example.dutydesk.entities.Handover;
import com.example.dutydesk.entities.Shift;
import com.example.dutydesk.entities.User;
import com.example.dutydesk.enums.HandoverStatus;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.exception.ConflictException;
import com.example.dutydesk.exception.ResourceNotFoundException;
import com.example.dutydesk.repository.HandoverRepository;
import com.example.dutydesk.repository.ShiftRepository;
import com.example.dutydesk.repository.UserRepository;
import com.example.dutydesk.service.HandoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HandoverServiceImpl implements HandoverService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);

    private final HandoverRepository handoverRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public HandoverListResponse getHandovers(String email, String status, String from, String to, String search) {
        Instant fromInstant = from != null ? Instant.parse(from + "T00:00:00Z") : null;
        Instant toInstant = to != null ? Instant.parse(to + "T23:59:59Z") : null;
        String statusParam = (status != null && !status.isBlank()) ? status : null;
        String searchParam = (search != null && !search.isBlank()) ? search : null;

        List<HandoverListResponse.HandoverItem> items;
        if (searchParam == null) {
            items = handoverRepository
                    .findForUserNoSearch(email, statusParam, fromInstant, toInstant)
                    .stream()
                    .map(this::toListItem)
                    .toList();
        } else {
            items = handoverRepository
                    .findForUser(email, statusParam, fromInstant, toInstant, searchParam)
                    .stream()
                    .map(this::toListItem)
                    .toList();
        }

        return new HandoverListResponse(items, new PaginationMeta(1, Math.max(1, items.size()), items.size(), 1));
    }

    @Override
    @Transactional(readOnly = true)
    public HandoverDetailResponse getHandoverById(String email, UUID id) {
        Handover handover = handoverRepository.findByIdAndFromUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Handover not found"));
        return toDetail(handover);
    }

    @Override
    @Transactional
    public CreateHandoverResponse createHandover(String email, CreateHandoverRequest request) {
        Shift shift = shiftRepository.findByIdAndUserEmail(request.shiftId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        if (handoverRepository.existsByShiftId(shift.getId())) {
            throw new ConflictException("Handover already exists for this shift");
        }

        User fromUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User toUser = null;
        if (request.toUserId() != null) {
            toUser = userRepository.findById(request.toUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
        }

        HandoverStatus status = parseStatus(request.status());
        Instant now = Instant.now();

        Handover handover = Handover.builder()
                .shift(shift)
                .fromUser(fromUser)
                .toUser(toUser)
                .incidents(request.incidents())
                .systemStatus(request.systemStatus())
                .pendingTasks(request.pendingTasks())
                .nextShiftInfo(request.nextShiftInfo())
                .additionalNotes(request.additionalNotes())
                .status(status)
                .submittedAt(status == HandoverStatus.SUBMITTED ? now : null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Handover saved = handoverRepository.save(handover);
        return new CreateHandoverResponse(saved.getId(), saved.getStatus().name().toLowerCase());
    }

    @Override
    @Transactional
    public HandoverDetailResponse updateHandover(String email, UUID id, UpdateHandoverRequest request) {
        Handover handover = handoverRepository.findByIdAndFromUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Handover not found"));

        if (handover.getStatus() == HandoverStatus.APPROVED || handover.getStatus() == HandoverStatus.REJECTED) {
            throw new BadRequestException("Approved or rejected handover cannot be updated");
        }

        if (request.incidents() != null) {
            handover.setIncidents(request.incidents());
        }
        if (request.systemStatus() != null) {
            handover.setSystemStatus(request.systemStatus());
        }
        if (request.pendingTasks() != null) {
            handover.setPendingTasks(request.pendingTasks());
        }
        if (request.nextShiftInfo() != null) {
            handover.setNextShiftInfo(request.nextShiftInfo());
        }
        if (request.additionalNotes() != null) {
            handover.setAdditionalNotes(request.additionalNotes());
        }

        if (request.toUserId() != null) {
            User toUser = userRepository.findById(request.toUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
            handover.setToUser(toUser);
        }

        if (request.status() != null && !request.status().isBlank()) {
            HandoverStatus updatedStatus = parseStatus(request.status());
            handover.setStatus(updatedStatus);
            if (updatedStatus == HandoverStatus.SUBMITTED && handover.getSubmittedAt() == null) {
                handover.setSubmittedAt(Instant.now());
            }
        }

        handover.setUpdatedAt(Instant.now());
        Handover saved = handoverRepository.save(handover);
        return toDetail(saved);
    }

    private HandoverStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return HandoverStatus.DRAFT;
        }
        try {
            return HandoverStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Invalid handover status", "status");
        }
    }

    private HandoverListResponse.HandoverItem toListItem(Handover handover) {
        String summary = handover.getSystemStatus();
        if (summary != null && summary.length() > 120) {
            summary = summary.substring(0, 120) + "...";
        }

        return new HandoverListResponse.HandoverItem(
                handover.getId(),
                handover.getShift().getShiftType().name().toLowerCase(),
                DATE_FORMAT.format(handover.getShift().getStartTime()),
                TIME_FORMAT.format(handover.getShift().getEndTime()),
                toListUser(handover.getFromUser()),
                handover.getToUser() == null ? null : toListUser(handover.getToUser()),
                handover.getStatus().name().toLowerCase(),
                summary);
    }

    private HandoverListResponse.UserSummary toListUser(User user) {
        return new HandoverListResponse.UserSummary(user.getId(), user.getFirstName() + " " + user.getLastName(), user.getPhone());
    }

    private HandoverDetailResponse toDetail(Handover handover) {
        HandoverDetailResponse.ShiftInfo shiftInfo = new HandoverDetailResponse.ShiftInfo(
                handover.getShift().getId(),
                handover.getShift().getShiftType().name().toLowerCase(),
                DATE_FORMAT.format(handover.getShift().getStartTime()),
                TIME_FORMAT.format(handover.getShift().getStartTime()),
                TIME_FORMAT.format(handover.getShift().getEndTime()));

        HandoverDetailResponse.UserInfo fromUser = new HandoverDetailResponse.UserInfo(
                handover.getFromUser().getId(),
                handover.getFromUser().getFirstName() + " " + handover.getFromUser().getLastName(),
                handover.getFromUser().getTeam() != null ? handover.getFromUser().getTeam().getName() : null,
                handover.getFromUser().getPhone());

        HandoverDetailResponse.UserInfo toUser = handover.getToUser() == null
                ? null
                : new HandoverDetailResponse.UserInfo(
                        handover.getToUser().getId(),
                        handover.getToUser().getFirstName() + " " + handover.getToUser().getLastName(),
                        handover.getToUser().getTeam() != null ? handover.getToUser().getTeam().getName() : null,
                        handover.getToUser().getPhone());

        HandoverDetailResponse.UserInfo approvedBy = handover.getApprovedBy() == null
                ? null
                : new HandoverDetailResponse.UserInfo(
                        handover.getApprovedBy().getId(),
                        handover.getApprovedBy().getFirstName() + " " + handover.getApprovedBy().getLastName(),
                        handover.getApprovedBy().getTeam() != null ? handover.getApprovedBy().getTeam().getName()
                                : null,
                        handover.getApprovedBy().getPhone());

        return new HandoverDetailResponse(
                handover.getId(),
                shiftInfo,
                fromUser,
                toUser,
                handover.getIncidents(),
                handover.getSystemStatus(),
                handover.getPendingTasks(),
                handover.getNextShiftInfo(),
                handover.getAdditionalNotes(),
                handover.getStatus().name().toLowerCase(),
                handover.getSubmittedAt(),
                handover.getApprovedAt(),
                approvedBy);
    }
}
