package com.example.dutydesk.repository;

import com.example.dutydesk.entities.Shift;
import com.example.dutydesk.enums.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    Optional<Shift> findFirstByUserEmailAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeDesc(
            String email,
            Instant nowForStart,
            Instant nowForEnd);

    Optional<Shift> findByIdAndUserEmail(UUID id, String email);

    @Query("""
            select s from Shift s
            where s.user.email = :email
                and (:status is null or s.status = :status)
                and s.startTime >= :from
                and s.startTime <= :to
            order by s.startTime desc
            """)
    List<Shift> findForUser(@Param("email") String email,
            @Param("status") ShiftStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to);

    long countByStatus(ShiftStatus status);

    List<Shift> findTop10ByStatusOrderByStartTimeDesc(ShiftStatus status);

    List<Shift> findByStartTimeBetween(Instant start, Instant end);

    List<Shift> findByTeamIdAndStartTimeBetween(UUID teamId, Instant start, Instant end);

    @Query("""
            select s from Shift s
            where (:email = '' or lower(s.user.email) = :email)
              and (:team is null or s.team.id = :team)
              and (:status is null or s.status = :status)
              and s.startTime >= :from
              and s.startTime <= :to
            order by s.startTime desc
            """)
    List<Shift> findForAdmin(@Param("email") String email,
            @Param("team") UUID team,
            @Param("status") ShiftStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("SELECT COUNT(s) FROM Shift s WHERE s.team.id = :teamId AND s.status = :status")
    long countByTeamIdAndStatus(@Param("teamId") UUID teamId, @Param("status") ShiftStatus status);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shift s WHERE s.team.id = :teamId AND s.startTime = :startTime")
    boolean existsByTeamIdAndStartTime(@Param("teamId") UUID teamId, @Param("startTime") Instant startTime);
}
