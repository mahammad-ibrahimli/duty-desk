package com.example.dutydesk.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 60, unique = true)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @OneToOne(optional = true)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt = java.time.Instant.now();

    @OneToMany(mappedBy = "team")
    private java.util.List<User> members;
}
