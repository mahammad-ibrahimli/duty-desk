package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.admin.CreateUserRequest;
import com.example.dutydesk.dto.response.admin.AdminDashboardResponse;
import com.example.dutydesk.entities.Team;
import com.example.dutydesk.enums.HandoverStatus;
import com.example.dutydesk.enums.ShiftStatus;
import com.example.dutydesk.exception.BadRequestException;
import com.example.dutydesk.repository.HandoverRepository;
import com.example.dutydesk.repository.ShiftRepository;
import com.example.dutydesk.repository.TeamRepository;
import com.example.dutydesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private HandoverRepository handoverRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getDashboard_ReturnsOverviewData() {
        when(userRepository.count()).thenReturn(12L);
        when(shiftRepository.countByStatus(ShiftStatus.ACTIVE)).thenReturn(3L);
        when(handoverRepository.countByStatus(HandoverStatus.SUBMITTED)).thenReturn(2L);
        when(shiftRepository.findTop10ByStatusOrderByStartTimeDesc(ShiftStatus.ACTIVE)).thenReturn(List.of());

        AdminDashboardResponse data = adminController.getDashboard().data();

        assertEquals(12L, data.overview().totalEmployees());
        assertEquals(3L, data.overview().activeShifts());
        assertEquals(2L, data.overview().pendingHandovers());
    }

    @Test
    void createUser_WhenRoleInvalid_ThrowsBadRequest() {
        UUID teamId = UUID.randomUUID();
        Team team = new Team();
        team.setId(teamId);
        team.setName("APM Team");
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        CreateUserRequest request = new CreateUserRequest(
                "new@example.com",
                "New",
                "User",
                "invalid-role",
                teamId,
                "+994501112233",
                "Temp12345");

        assertThrows(BadRequestException.class, () -> adminController.createUser(request));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
