package com.example.dutydesk.service;

import com.example.dutydesk.dto.request.shift.AddShiftNoteRequest;
import com.example.dutydesk.dto.request.shift.CheckInRequest;
import com.example.dutydesk.dto.request.shift.CheckOutRequest;
import com.example.dutydesk.dto.request.shift.CreateShiftChangeRequest;
import com.example.dutydesk.dto.response.shift.*;

import java.time.LocalDate;
import java.util.UUID;
import com.example.dutydesk.dto.request.admin.GenerateScheduleRequest;

public interface ShiftService {
    ShiftListResponse getShifts(String email, String status, LocalDate from, LocalDate to, int page, int limit);

    CurrentShiftResponse getCurrentShift(String email);

    void generateMonthlySchedule(GenerateScheduleRequest request);

    CheckInOutResponse checkIn(String email, CheckInRequest request);

    CheckInOutResponse checkOut(String email, CheckOutRequest request);

    ShiftChangeRequestResponse createChangeRequest(String email, CreateShiftChangeRequest request);

    ShiftNoteResponse getShiftNotes(String email, UUID shiftId);

    void addShiftNote(String email, UUID shiftId, AddShiftNoteRequest request);
}
