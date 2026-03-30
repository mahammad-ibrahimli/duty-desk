package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.admin.CreateUserRequest;
import com.example.dutydesk.dto.request.admin.UpdateUserRequest;
import com.example.dutydesk.dto.request.admin.UpsertScheduleRequest;
import com.example.dutydesk.dto.response.common.ApiResponse;
import com.example.dutydesk.dto.response.common.PaginationMeta;
import com.example.dutydesk.dto.response.admin.AdminDashboardResponse;
import com.example.dutydesk.dto.response.admin.AdminUsersListResponse;
import com.example.dutydesk.dto.response.admin.ScheduleResponse;
import com.example.dutydesk.dto.response.shift.ShiftItemDto;
import com.example.dutydesk.dto.response.shift.ShiftListResponse;
import com.example.dutydesk.entities.Shift;
import com.example.dutydesk.entities.Team;
import com.example.dutydesk.entities.User;
import com.example.dutydesk.enums.HandoverStatus;
import com.example.dutydesk.enums.Role;
import com.example.dutydesk.enums.ShiftStatus;
import com.example.dutydesk.enums.ShiftType;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.exception.ResourceNotFoundException;
import com.example.dutydesk.repository.HandoverRepository;
import com.example.dutydesk.repository.ShiftRepository;
import com.example.dutydesk.repository.TeamRepository;
import com.example.dutydesk.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.example.dutydesk.dto.response.admin.TeamStatsResponse;

import com.example.dutydesk.dto.request.admin.GenerateScheduleRequest;
import com.example.dutydesk.service.ShiftService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
public class AdminController {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ShiftRepository shiftRepository;
    private final HandoverRepository handoverRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShiftService shiftService;


    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> getDashboard() {
        AdminDashboardResponse.Overview overview = new AdminDashboardResponse.Overview(
                userRepository.count(),
                shiftRepository.countByStatus(ShiftStatus.ACTIVE),
                handoverRepository.countByStatus(HandoverStatus.SUBMITTED),
                shiftRepository.countByStatus(ShiftStatus.ACTIVE));

        List<AdminDashboardResponse.OnDutyItem> onDutyNow = shiftRepository
                .findTop10ByStatusOrderByStartTimeDesc(ShiftStatus.ACTIVE)
                .stream()
                .map(shift -> new AdminDashboardResponse.OnDutyItem(
                        shift.getUser().getId(),
                        shift.getUser().getFirstName() + " " + shift.getUser().getLastName(),
                        shift.getTeam() != null ? shift.getTeam().getName() : null,
                        shift.getShiftType().name().toLowerCase(),
                        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).format(shift.getStartTime()),
                        shift.getStatus().name().toLowerCase()))
                .toList();

        AdminDashboardResponse.WeeklyStats weeklyStats = new AdminDashboardResponse.WeeklyStats(
                List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                List.of(0, 0, 0, 0, 0, 0, 0),
                List.of(0, 0, 0, 0, 0, 0, 0));

        AdminDashboardResponse response = new AdminDashboardResponse(
                overview,
                onDutyNow,
                new ArrayList<>(),
                weeklyStats);
        return ApiResponse.success(response);
    }

    @PostMapping("/schedules/generate")
    public ApiResponse<Void> generateSchedule(@RequestBody @Valid GenerateScheduleRequest request) {
        shiftService.generateMonthlySchedule(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/teams")
    @Transactional(readOnly = true)
    public ApiResponse<List<TeamStatsResponse>> getTeams() {
        List<TeamStatsResponse> teams = teamRepository.findAll().stream()
                .map(team -> new TeamStatsResponse(
                        team.getId(),
                        team.getName(),
                        team.getDescription(),
                        userRepository.countByTeamId(team.getId()),
                        shiftRepository.countByTeamIdAndStatus(team.getId(), ShiftStatus.ACTIVE)))
                .toList();
        return ApiResponse.success(teams);
    }

    @GetMapping("/teams/{id}")
    @Transactional(readOnly = true)
    public ApiResponse<AdminUsersListResponse.TeamSummary> getTeamById(@PathVariable UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        AdminUsersListResponse.TeamSummary dto = new AdminUsersListResponse.TeamSummary(team.getId(), team.getName());
        return ApiResponse.success(dto);
    }

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ApiResponse<AdminUsersListResponse> getUsers(@RequestParam(required = false) String role,
            @RequestParam(required = false) UUID team,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<User> users = userRepository.findAll();
        if (users == null) {
            users = List.of();
        }
        List<AdminUsersListResponse.AdminUserItem> allItems = users.stream()
                .filter(user -> role == null || user.getRole().name().equalsIgnoreCase(role))
                .filter(user -> team == null || (user.getTeam() != null && team.equals(user.getTeam().getId())))
                .filter(user -> {
                    if (status == null) {
                        return true;
                    }
                    return "active".equalsIgnoreCase(status) ? user.isActive() : !user.isActive();
                })
                .filter(user -> {
                    if (search == null || search.isBlank()) {
                        return true;
                    }
                    String keyword = search.toLowerCase(Locale.ROOT);
                    String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase(Locale.ROOT);
                    return user.getEmail().toLowerCase(Locale.ROOT).contains(keyword) || fullName.contains(keyword);
                })
                .map(user -> new AdminUsersListResponse.AdminUserItem(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole().name().toLowerCase(),
                        user.isActive(),
                        user.getTeam() == null ? null
                                : new AdminUsersListResponse.TeamSummary(user.getTeam().getId(),
                                        user.getTeam().getName()),
                        user.getPhone()))
                .toList();

        long total = allItems.size();
        int safeLimit = Math.max(1, limit);
        int safePage = Math.max(1, page);
        int totalPages = (int) Math.ceil((double) total / safeLimit);
        int skip = Math.max(0, (safePage - 1) * safeLimit);
        List<AdminUsersListResponse.AdminUserItem> pagedItems = allItems.stream()
                .skip(skip)
                .limit(safeLimit)
                .toList();

        return ApiResponse.success(new AdminUsersListResponse(pagedItems,
                new PaginationMeta(page, limit, total, totalPages)));
    }

    @GetMapping("/shifts")
    @Transactional(readOnly = true)
    public ApiResponse<ShiftListResponse> getShiftsAdmin(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID team,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (page < 1)
            page = 1;
        if (limit < 1)
            limit = 10;

        Instant fromInstant = (from == null ? LocalDate.of(1970, 1, 1) : from).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toInstant = (to == null ? LocalDate.of(9999, 12, 31) : to).plusDays(1).atStartOfDay().minusNanos(1)
                .toInstant(ZoneOffset.UTC);
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        ShiftStatus shiftStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                shiftStatus = ShiftStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException("Invalid status", "status");
            }
        }

        List<Shift> all = shiftRepository.findForAdmin(normalizedEmail, team, shiftStatus, fromInstant, toInstant);
        long total = all.size();
        int fromIndex = Math.min((page - 1) * limit, all.size());
        int toIndex = Math.min(fromIndex + limit, all.size());

        List<ShiftItemDto> items = all.subList(fromIndex, toIndex)
                .stream()
                .map(this::toShiftItem)
                .toList();

        int totalPages = (int) Math.ceil(total / (double) limit);
        return ApiResponse.success(new ShiftListResponse(items, new PaginationMeta(page, limit, total, totalPages)));
    }

    @PostMapping("/users")
    public ApiResponse<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Team teamEntity = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Role role = parseRole(request.role());
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(role)
                .team(teamEntity)
                .phone(request.phone())
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User saved = userRepository.save(user);
        return ApiResponse.success(Map.of("id", saved.getId()));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> updateUser(@PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.firstName() != null)
            user.setFirstName(request.firstName());
        if (request.lastName() != null)
            user.setLastName(request.lastName());
        if (request.phone() != null)
            user.setPhone(request.phone());
        if (request.role() != null)
            user.setRole(parseRole(request.role()));
        if (request.teamId() != null) {
            Team teamEntity = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            user.setTeam(teamEntity);
        }
        if (request.isActive() != null)
            user.setActive(request.isActive());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return ApiResponse.success(Map.of("id", user.getId()));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> deleteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return ApiResponse.success(Map.of("id", user.getId(), "isActive", false));
    }

    @GetMapping("/schedules")
    public ApiResponse<ScheduleResponse> getSchedules(@RequestParam String week,
            @RequestParam(required = false) UUID team) {
        LocalDate monday = parseIsoWeekToMonday(week);
        LocalDate sunday = monday.plusDays(6);
        Instant start = monday.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = sunday.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);

        List<Shift> shifts = team == null
                ? shiftRepository.findByStartTimeBetween(start, end)
                : shiftRepository.findByTeamIdAndStartTimeBetween(team, start, end);

        Map<LocalDate, Map<String, List<ScheduleResponse.UserShiftItem>>> grouped = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            Map<String, List<ScheduleResponse.UserShiftItem>> dayShifts = new LinkedHashMap<>();
            dayShifts.put("day", new ArrayList<>());
            dayShifts.put("evening", new ArrayList<>());
            dayShifts.put("night", new ArrayList<>());
            grouped.put(day, dayShifts);
        }

        for (Shift shift : shifts) {
            LocalDate day = shift.getStartTime().atOffset(ZoneOffset.UTC).toLocalDate();
            Map<String, List<ScheduleResponse.UserShiftItem>> dayShifts = grouped.get(day);
            if (dayShifts == null)
                continue;
            String key = shift.getShiftType().name().toLowerCase();
            dayShifts.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(new ScheduleResponse.UserShiftItem(
                            shift.getUser().getId(),
                            shift.getUser().getFirstName() + " " + shift.getUser().getLastName()));
        }

        List<ScheduleResponse.ScheduleDay> days = grouped.entrySet().stream()
                .map(entry -> new ScheduleResponse.ScheduleDay(
                        entry.getKey().toString(),
                        entry.getKey().getDayOfWeek().name(),
                        entry.getValue()))
                .toList();

        return ApiResponse.success(new ScheduleResponse(week, days));
    }

    @PostMapping("/schedules")
    public ApiResponse<Map<String, Object>> upsertSchedules(@Valid @RequestBody UpsertScheduleRequest request) {
        List<UUID> created = new ArrayList<>();
        for (UpsertScheduleRequest.ShiftInput input : request.shifts()) {
            User user = userRepository.findById(input.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (user.getRole() != Role.EMPLOYEE) {
                throw new BadRequestException("Only employees can be assigned to shifts", "userId");
            }
            ShiftType shiftType = parseShiftType(input.type());

            Instant start = input.date().atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant end = start.plusSeconds(8 * 3600);
            if (shiftType == ShiftType.DAY) {
                start = input.date().atTime(8, 0).toInstant(ZoneOffset.UTC);
                end = input.date().atTime(16, 0).toInstant(ZoneOffset.UTC);
            } else if (shiftType == ShiftType.EVENING) {
                start = input.date().atTime(16, 0).toInstant(ZoneOffset.UTC);
                end = input.date().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
            }

            Shift shift = Shift.builder()
                    .user(user)
                    .team(user.getTeam())
                    .shiftType(shiftType)
                    .startTime(start)
                    .endTime(end)
                    .status(ShiftStatus.SCHEDULED)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            created.add(shiftRepository.save(shift).getId());
        }

        return ApiResponse.success(Map.of("createdShiftIds", created));
    }

    @DeleteMapping("/schedules/{id}")
    public ApiResponse<Map<String, Object>> cancelScheduledShift(@PathVariable UUID id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        if (shift.getStatus() != ShiftStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled shifts can be cancelled", "status");
        }
        shift.setStatus(ShiftStatus.CANCELLED);
        shift.setUpdatedAt(Instant.now());
        shiftRepository.save(shift);
        return ApiResponse.success(Map.of("id", shift.getId(), "status", "cancelled"));
    }

    private Role parseRole(String value) {
        try {
            return Role.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            throw new BadRequestException("Invalid role", "role");
        }
    }

    private ShiftType parseShiftType(String value) {
        try {
            return ShiftType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            throw new BadRequestException("Invalid shift type", "type");
        }
    }

    private ShiftItemDto toShiftItem(Shift shift) {
        ShiftItemDto.CheckinDto checkinDto = null;
        if (shift.getCheckin() != null) {
            checkinDto = new ShiftItemDto.CheckinDto(
                    TIME_FORMAT.format(shift.getCheckin().getCheckInTime()),
                    shift.getCheckin().getCheckOutTime() != null
                            ? TIME_FORMAT.format(shift.getCheckin().getCheckOutTime())
                            : null,
                    shift.getCheckin().getStatus().name().toLowerCase());
        }

        String label = switch (shift.getShiftType()) {
            case DAY -> "Gündüz";
            case EVENING -> "Axşam";
            case NIGHT -> "Gecə";
        };

        return new ShiftItemDto(
                shift.getId(),
                shift.getUser().getId(),
                shift.getUser().getFirstName() + " " + shift.getUser().getLastName(),
                shift.getTeam() != null ? shift.getTeam().getName() : null,
                shift.getShiftType().name().toLowerCase(),
                label,
                DATE_FORMAT.format(shift.getStartTime()),
                TIME_FORMAT.format(shift.getStartTime()),
                TIME_FORMAT.format(shift.getEndTime()),
                shift.getStatus().name().toLowerCase(),
                checkinDto);
    }

    private LocalDate parseIsoWeekToMonday(String isoWeek) {
        try {
            String[] parts = isoWeek.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            return LocalDate.of(year, 1, 4)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                    .with(java.time.DayOfWeek.MONDAY);
        } catch (Exception exception) {
            throw new BadRequestException("Invalid week format. Expected YYYY-Www", "week");
        }
    }
}
