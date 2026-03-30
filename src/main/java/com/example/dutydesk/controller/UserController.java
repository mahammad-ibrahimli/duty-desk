package com.example.dutydesk.controller;

import com.example.dutydesk.dto.response.common.ApiResponse;
import com.example.dutydesk.dto.response.user.CurrentUserResponse;
import com.example.dutydesk.exception.UnauthorizedException;
import com.example.dutydesk.service.UserService;
import lombok.RequiredArgsConstructor;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return ApiResponse.success(userService.getCurrentUser(principal.getName()));
    }
}
