package com.example.dutydesk.repository;

import com.example.dutydesk.entities.User;
import com.example.dutydesk.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.team WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.team.id = :teamId")
    long countByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT u FROM User u WHERE u.team.id = :teamId")
    List<User> findByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT u FROM User u WHERE u.team.id = :teamId AND u.role = :role")
    List<User> findByTeamIdAndRole(@Param("teamId") UUID teamId, @Param("role") Role role);
}
