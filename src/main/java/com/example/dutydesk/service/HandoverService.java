package com.example.dutydesk.service;

import com.example.dutydesk.dto.request.handover.CreateHandoverRequest;
import com.example.dutydesk.dto.request.handover.UpdateHandoverRequest;
import com.example.dutydesk.dto.response.handover.CreateHandoverResponse;
import com.example.dutydesk.dto.response.handover.HandoverDetailResponse;
import com.example.dutydesk.dto.response.handover.HandoverListResponse;

import java.util.UUID;

public interface HandoverService {

    HandoverListResponse getHandovers(String email, String status, String from, String to, String search);

    HandoverDetailResponse getHandoverById(String email, UUID id);

    CreateHandoverResponse createHandover(String email, CreateHandoverRequest request);

    HandoverDetailResponse updateHandover(String email, UUID id, UpdateHandoverRequest request);
}
