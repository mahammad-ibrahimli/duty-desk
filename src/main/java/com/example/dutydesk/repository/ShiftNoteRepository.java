package com.example.dutydesk.repository;

import com.example.dutydesk.entities.ShiftNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShiftNoteRepository extends JpaRepository<ShiftNote, UUID> {
    List<ShiftNote> findByShiftIdOrderByCreatedAtDesc(UUID shiftId);
}
