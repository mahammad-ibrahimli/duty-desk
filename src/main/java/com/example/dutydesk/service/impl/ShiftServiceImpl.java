package com.example.dutydesk.service.impl;

import com.example.dutydesk.dto.request.admin.GenerateScheduleRequest;
import com.example.dutydesk.dto.request.shift.AddShiftNoteRequest;
import com.example.dutydesk.dto.request.shift.CheckInRequest;
import com.example.dutydesk.dto.request.shift.CheckOutRequest;
import com.example.dutydesk.dto.request.shift.CreateShiftChangeRequest;
import com.example.dutydesk.dto.response.common.PaginationMeta;
import com.example.dutydesk.dto.response.shift.*;
import com.example.dutydesk.entities.*;
import com.example.dutydesk.enums.CheckinStatus;
import com.example.dutydesk.enums.Role;
import com.example.dutydesk.enums.ShiftChangeRequestStatus;
import com.example.dutydesk.enums.ShiftStatus;
import com.example.dutydesk.enums.ShiftType;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.exception.ConflictException;
import com.example.dutydesk.exception.ResourceNotFoundException;
import com.example.dutydesk.repository.*;
import com.example.dutydesk.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);

    private final ShiftRepository shiftRepository;
    private final CheckinRepository checkinRepository;
    private final ShiftNoteRepository shiftNoteRepository;
    private final ShiftChangeRequestRepository shiftChangeRequestRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void generateMonthlySchedule(GenerateScheduleRequest request) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        List<User> users = userRepository.findByTeamIdAndRole(request.teamId(), Role.EMPLOYEE);
        if (users.isEmpty()) {
            throw new BadRequestException("Team has no employees");
        }
        users.sort(Comparator.comparing(User::getLastName)); // Ensure consistent order

        LocalDate startOfMonth = LocalDate.of(request.year(), request.month(), 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        int daysInMonth = endOfMonth.getDayOfMonth();

        int userIndex = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = startOfMonth.withDayOfMonth(day);

            // Shift patterns based on team
            List<ShiftType> patterns = getPatternsForTeam(team.getName());
            
            for (ShiftType type : patterns) {
                // Determine times based on type (simplified logic, ideally should be config)
                Instant start = getStartTime(currentDate, type);
                Instant end = getEndTime(currentDate, type);

                User assignedUser = users.get(userIndex % users.size());
                userIndex++;

                // Check if shift already exists to avoid duplicates
                boolean exists = shiftRepository.existsByTeamIdAndStartTime(team.getId(), start);
                if (!exists) {
                    Shift shift = Shift.builder()
                            .user(assignedUser)
                            .team(team)
                            .shiftType(type)
                            .startTime(start)
                            .endTime(end)
                            .status(ShiftStatus.SCHEDULED)
                            .createdAt(Instant.now())
                            .build();
                    shiftRepository.save(shift);
                }
            }
            
            // If users count is divisible by patterns count (e.g. 3 users, 3 shifts),
            // shift the rotation by 1 person each day to ensure fairness.
            if (users.size() > 0 && patterns.size() > 0 && users.size() % patterns.size() == 0) {
                 userIndex++;
            }
        }
    }

    private List<ShiftType> getPatternsForTeam(String teamName) {
        // Hardcoded patterns based on project requirements.
        // In future, this can be moved to DB configuration.
        if (teamName.toUpperCase().contains("APM")) {
            return List.of(ShiftType.DAY, ShiftType.EVENING, ShiftType.NIGHT);
        } else if (teamName.toUpperCase().contains("NOC")) {
            return List.of(ShiftType.DAY, ShiftType.EVENING, ShiftType.NIGHT);
        } else if (teamName.toUpperCase().contains("SOC")) {
            return List.of(ShiftType.DAY, ShiftType.EVENING, ShiftType.NIGHT);
        }
        // Default pattern
        return List.of(ShiftType.DAY, ShiftType.EVENING, ShiftType.NIGHT);
    }

    private Instant getStartTime(LocalDate date, ShiftType type) {
        return switch (type) {
            case DAY -> date.atTime(8, 0).toInstant(ZoneOffset.UTC);
            case EVENING -> date.atTime(16, 0).toInstant(ZoneOffset.UTC);
            case NIGHT -> date.atTime(0, 0).toInstant(ZoneOffset.UTC); // Actually 00:00 of date
        };
    }

    private Instant getEndTime(LocalDate date, ShiftType type) {
        return switch (type) {
            case DAY -> date.atTime(16, 0).toInstant(ZoneOffset.UTC);
            case EVENING -> date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC); // 00:00 next day
            case NIGHT -> date.atTime(8, 0).toInstant(ZoneOffset.UTC);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftListResponse getShifts(String email, String status, LocalDate from, LocalDate to, int page, int limit) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;

        Instant fromInstant = (from == null ? LocalDate.of(1970, 1, 1) : from).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toInstant = (to == null ? LocalDate.of(9999, 12, 31) : to).plusDays(1).atStartOfDay().minusNanos(1)
                .toInstant(ZoneOffset.UTC);

        ShiftStatus shiftStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                shiftStatus = ShiftStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }

        List<Shift> all = shiftRepository.findForUser(email, shiftStatus, fromInstant, toInstant);
        long total = all.size();

        int fromIndex = Math.min((page - 1) * limit, all.size());
        int toIndex = Math.min(fromIndex + limit, all.size());

        List<ShiftItemDto> items = all.subList(fromIndex, toIndex)
                .stream()
                .map(this::toShiftItem)
                .toList();

        int totalPages = (int) Math.ceil(total / (double) limit);
        return new ShiftListResponse(items, new PaginationMeta(page, limit, total, totalPages));
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentShiftResponse getCurrentShift(String email) {
        Instant now = Instant.now();
    
        Shift shift = shiftRepository
                .findFirstByUserEmailAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeDesc(
                        email,
                        now,
                        now)
                .orElseThrow(() -> new ResourceNotFoundException("No active shift found"));

        long remaining = Math.max(0, Duration.between(now, shift.getEndTime()).getSeconds());
        CurrentShiftResponse.CheckinInfo checkinInfo = null;
        
        if (shift.getCheckin() != null) {
            checkinInfo = new CurrentShiftResponse.CheckinInfo(
                shift.getCheckin().getCheckInTime(), 
                shift.getCheckin().getStatus().name().toLowerCase()
            );
        }

        return new CurrentShiftResponse(
                shift.getId(),
                shift.getShiftType().name().toLowerCase(),
                shift.getStartTime(),
                shift.getEndTime(),
                remaining,
                shift.getStatus().name().toLowerCase(),
                checkinInfo);
    }

    @Override
    @Transactional
    public CheckInOutResponse checkIn(String email, CheckInRequest request) {
        Shift shift = shiftRepository.findByIdAndUserEmail(request.shiftId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        if (shift.getCheckin() != null) {
             throw new ConflictException("Already checked in");
        }
        
        if (shift.getStatus() == ShiftStatus.COMPLETED || shift.getStatus() == ShiftStatus.CANCELLED) {
            throw new BadRequestException("Shift is not active");
        }

        Instant now = Instant.now();
        
        Checkin checkin = Checkin.builder()
                .shift(shift)
                .user(shift.getUser())
                .checkInTime(now)
                .checkInNote(request.note())
                .status(CheckinStatus.CHECKED_IN)
                .createdAt(now)
                .build();
        
        checkinRepository.save(checkin);

        shift.setStatus(ShiftStatus.ACTIVE);
        shift.setUpdatedAt(now);
    
        shiftRepository.save(shift);

        return new CheckInOutResponse(shift.getId(), now, null, null);
    }

    @Override
    @Transactional
    public CheckInOutResponse checkOut(String email, CheckOutRequest request) {
        Shift shift = shiftRepository.findByIdAndUserEmail(request.shiftId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        if (shift.getStatus() != ShiftStatus.ACTIVE) {
            throw new BadRequestException("Shift is not active");
        }
        
        Checkin checkin = shift.getCheckin();
        if (checkin == null) {
            throw new ResourceNotFoundException("No checkin found for this shift");
        }

        Instant now = Instant.now();
        checkin.setCheckOutTime(now);
        checkin.setCheckOutNote(request.note());
        checkin.setStatus(CheckinStatus.CHECKED_OUT);
        checkinRepository.save(checkin);

        shift.setStatus(ShiftStatus.COMPLETED);
        shift.setUpdatedAt(now);
        shiftRepository.save(shift);

        double hours = Duration.between(checkin.getCheckInTime(), now).toMinutes() / 60.0;
        double totalHours = Math.round(hours * 100.0) / 100.0;

        return new CheckInOutResponse(shift.getId(), checkin.getCheckInTime(), now, totalHours);
    }

    @Override
    @Transactional
    public ShiftChangeRequestResponse createChangeRequest(String email, CreateShiftChangeRequest request) {
        Shift shift = shiftRepository.findByIdAndUserEmail(request.shiftId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        
        ShiftChangeRequest changeRequest = ShiftChangeRequest.builder()
                .shift(shift)
                .user(shift.getUser())
                .reason(request.reason())
                .requestedDate(request.requestedDate())
                .status(ShiftChangeRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        
        shiftChangeRequestRepository.save(changeRequest);
        
        return new ShiftChangeRequestResponse(changeRequest.getId(), changeRequest.getStatus().name().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftNoteResponse getShiftNotes(String email, UUID shiftId) {
       
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        
        
        List<ShiftNote> notes = shiftNoteRepository.findByShiftIdOrderByCreatedAtDesc(shiftId);
        List<ShiftNoteItem> items = notes.stream()
                .map(note -> new ShiftNoteItem(note.getId(), note.getContent(), note.getCreatedAt()))
                .toList();
        
        return new ShiftNoteResponse(items);
    }

    @Override
    @Transactional
    public void addShiftNote(String email, UUID shiftId, AddShiftNoteRequest request) {
        Shift shift = shiftRepository.findByIdAndUserEmail(shiftId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        
        ShiftNote note = ShiftNote.builder()
                .shift(shift)
                .user(shift.getUser())
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        
        shiftNoteRepository.save(note);
    }

    private ShiftItemDto toShiftItem(Shift shift) {
        ShiftItemDto.CheckinDto checkinDto = null;
        if (shift.getCheckin() != null) {
            Checkin c = shift.getCheckin();
            checkinDto = new ShiftItemDto.CheckinDto(
                    TIME_FORMAT.format(c.getCheckInTime()),
                    c.getCheckOutTime() != null ? TIME_FORMAT.format(c.getCheckOutTime()) : null,
                    c.getStatus().name().toLowerCase());
        }

        return new ShiftItemDto(
                shift.getId(),
                shift.getUser().getId(),
                shift.getUser().getFirstName() + " " + shift.getUser().getLastName(),
                shift.getTeam() != null ? shift.getTeam().getName() : null,
                shift.getShiftType().name().toLowerCase(),
                typeLabel(shift.getShiftType()),
                DATE_FORMAT.format(shift.getStartTime()),
                TIME_FORMAT.format(shift.getStartTime()),
                TIME_FORMAT.format(shift.getEndTime()),
                shift.getStatus().name().toLowerCase(),
                checkinDto);
    }

    private String typeLabel(ShiftType shiftType) {
        return switch (shiftType) {
            case DAY -> "Gündüz";
            case EVENING -> "Axşam";
            case NIGHT -> "Gecə";
        };
    }
}
