package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.shift.CheckInRequest;
import com.example.dutydesk.dto.response.common.PaginationMeta;
import com.example.dutydesk.dto.response.shift.CheckInOutResponse;
import com.example.dutydesk.dto.response.shift.CurrentShiftResponse;
import com.example.dutydesk.dto.response.shift.ShiftItemDto;
import com.example.dutydesk.dto.response.shift.ShiftListResponse;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.service.ShiftService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftControllerTest {

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private ShiftController shiftController;

    @Test
    void current_WhenNoAuthenticatedUser_ThrowsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> shiftController.current(null));
    }

    @Test
    void getShifts_WhenAuthenticated_DelegatesToService() {
        UserDetails userDetails = User.withUsername("leyla@example.com").password("x").roles("EMPLOYEE").build();
        ShiftListResponse response = new ShiftListResponse(
                List.of(new ShiftItemDto(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Leyla Məmmədova",
                        "APM Team",
                        "day",
                        "Gündüz",
                        "2026-02-17",
                        "08:00",
                        "16:00",
                        "active",
                        null)),
                new PaginationMeta(1, 10, 1, 1));

        when(shiftService.getShifts("leyla@example.com", "active", null, null, 1, 10)).thenReturn(response);

        ShiftListResponse result = shiftController.getShifts(userDetails, "active", null, null, 1, 10).data();

        assertEquals(1, result.shifts().size());
        verify(shiftService).getShifts("leyla@example.com", "active", null, null, 1, 10);
    }

    @Test
    void checkIn_WhenAuthenticated_DelegatesToService() {
        UserDetails userDetails = User.withUsername("leyla@example.com").password("x").roles("EMPLOYEE").build();
        UUID shiftId = UUID.randomUUID();
        CheckInRequest request = new CheckInRequest(shiftId, "Arrived");
        CheckInOutResponse response = new CheckInOutResponse(UUID.randomUUID(), Instant.now(), null, null);

        when(shiftService.checkIn("leyla@example.com", request)).thenReturn(response);

        CheckInOutResponse result = shiftController.checkIn(userDetails, request).data();

        assertEquals(response.checkinId(), result.checkinId());
        verify(shiftService).checkIn("leyla@example.com", request);
    }
}
