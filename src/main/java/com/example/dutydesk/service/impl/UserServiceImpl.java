package com.example.dutydesk.service.impl;

import com.example.dutydesk.dto.response.user.CurrentShiftDto;
import com.example.dutydesk.dto.response.user.CurrentUserResponse;
import com.example.dutydesk.dto.response.user.TeamSummaryDto;
import com.example.dutydesk.entities.Shift;
import com.example.dutydesk.entities.User;
import com.example.dutydesk.exception.ResourceNotFoundException;
import com.example.dutydesk.repository.ShiftRepository;
import com.example.dutydesk.repository.UserRepository;
import com.example.dutydesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public CurrentUserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Instant now = Instant.now();
        Optional<Shift> currentShift = shiftRepository
                .findFirstByUserEmailAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeDesc(
                        email,
                        now,
                        now);

        CurrentShiftDto currentShiftDto = currentShift
                .map(shift -> new CurrentShiftDto(
                        shift.getId(),
                        shift.getShiftType().name().toLowerCase(),
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getStatus().name().toLowerCase()))
                .orElse(null);

        TeamSummaryDto teamSummaryDto = new TeamSummaryDto(user.getTeam().getId(), user.getTeam().getName());

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name().toLowerCase(),
                user.getPhone(),
                teamSummaryDto,
                currentShiftDto);
    }
}
