package com.example.dutydesk.entities;

import com.example.dutydesk.enums.ShiftStatus;
import com.example.dutydesk.enums.ShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType shiftType;

    @Column(nullable = false)
    private java.time.Instant startTime;

    @Column(nullable = false)
    private java.time.Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.SCHEDULED;

    private String notes;

    @OneToOne(mappedBy = "shift", cascade = CascadeType.ALL)
    private Checkin checkin;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL)
    private java.util.List<ShiftNote> shiftNotes;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL)
    private java.util.List<ShiftChangeRequest> changeRequests;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private java.time.Instant createdAt = java.time.Instant.now();

    private java.time.Instant updatedAt;
}
