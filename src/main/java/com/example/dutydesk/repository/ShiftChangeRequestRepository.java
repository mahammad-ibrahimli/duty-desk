package com.example.dutydesk.repository;

import com.example.dutydesk.entities.ShiftChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShiftChangeRequestRepository extends JpaRepository<ShiftChangeRequest, UUID> {
}
