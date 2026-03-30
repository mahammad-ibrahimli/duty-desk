package com.example.dutydesk.controller;

import com.example.dutydesk.dto.request.handover.UpdateHandoverRequest;
import com.example.dutydesk.dto.response.handover.HandoverDetailResponse;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.service.HandoverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandoverControllerTest {

    @Mock
    private HandoverService handoverService;

    @InjectMocks
    private HandoverController handoverController;

    @Test
    void getHandovers_WhenNoAuthenticatedUser_ThrowsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> handoverController.getHandovers(null, null, null, null, null));
    }

    @Test
    void updateHandover_WhenAuthenticated_DelegatesToService() {
        UUID handoverId = UUID.randomUUID();
        UserDetails userDetails = User.withUsername("leyla@example.com").password("x").roles("EMPLOYEE").build();
        UpdateHandoverRequest request = new UpdateHandoverRequest(
                "incidents",
                "ok",
                "tasks",
                "next",
                "notes",
                "submitted",
                null);

        HandoverDetailResponse response = new HandoverDetailResponse(
                handoverId,
                null,
                null,
                null,
                "incidents",
                "ok",
                "tasks",
                "next",
                "notes",
                "submitted",
                null,
                null,
                null);

        when(handoverService.updateHandover("leyla@example.com", handoverId, request)).thenReturn(response);

        HandoverDetailResponse result = handoverController.updateHandover(userDetails, handoverId, request).data();

        assertEquals(handoverId, result.id());
        verify(handoverService).updateHandover("leyla@example.com", handoverId, request);
    }
}
