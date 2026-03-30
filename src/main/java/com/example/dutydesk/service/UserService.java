package com.example.dutydesk.service;

import com.example.dutydesk.dto.response.user.CurrentUserResponse;

public interface UserService {
    CurrentUserResponse getCurrentUser(String email);
}
