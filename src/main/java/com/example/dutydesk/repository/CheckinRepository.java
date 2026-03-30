package com.example.dutydesk.repository;

import com.example.dutydesk.entities.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckinRepository extends JpaRepository<Checkin, UUID> {
}
