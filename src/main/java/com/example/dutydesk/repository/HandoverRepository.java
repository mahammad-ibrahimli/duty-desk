package com.example.dutydesk.repository;

import com.example.dutydesk.entities.Handover;
import com.example.dutydesk.enums.HandoverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HandoverRepository extends JpaRepository<Handover, UUID> {

    List<Handover> findAllByFromUserEmailOrderByCreatedAtDesc(String email);

    Optional<Handover> findByIdAndFromUserEmail(UUID id, String email);

    boolean existsByShiftId(UUID shiftId);

    long countByStatus(HandoverStatus status);

    @Query("""
            select h from Handover h
            where h.fromUser.email = :email
                and (:status is null or lower(cast(h.status as string)) = lower(:status))
                and (h.createdAt >= coalesce(:from, h.createdAt))
                and (h.createdAt <= coalesce(:to, h.createdAt))
                and (
                     lower(h.incidents) like lower(concat('%', :search, '%'))
                     or lower(h.systemStatus) like lower(concat('%', :search, '%'))
                     or lower(h.pendingTasks) like lower(concat('%', :search, '%'))
                )
            order by h.createdAt desc
            """)
    List<Handover> findForUser(@Param("email") String email,
            @Param("status") String status,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("search") String search);

    @Query("""
            select h from Handover h
            where h.fromUser.email = :email
                and (:status is null or lower(cast(h.status as string)) = lower(:status))
                and (h.createdAt >= coalesce(:from, h.createdAt))
                and (h.createdAt <= coalesce(:to, h.createdAt))
            order by h.createdAt desc
            """)
    List<Handover> findForUserNoSearch(@Param("email") String email,
            @Param("status") String status,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
