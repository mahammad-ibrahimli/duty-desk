package com.example.dutydesk.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

// Daxili paket
import com.example.dutydesk.enums.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(nullable = false, name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "user")
    private List<Shift> shifts;

    @Column(nullable = true, length = 20, unique = true)
    private String phone; 

    @Column(nullable = true, length = 255)
    private String avatarUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt = java.time.Instant.now();

    private java.time.Instant updatedAt;

}
