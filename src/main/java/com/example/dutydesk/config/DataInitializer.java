package com.example.dutydesk.config;

import com.example.dutydesk.entities.*;
import com.example.dutydesk.enums.*;
import com.example.dutydesk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ShiftRepository shiftRepository;
    private final CheckinRepository checkinRepository;
    private final ShiftNoteRepository shiftNoteRepository;
    private final ShiftChangeRequestRepository shiftChangeRequestRepository;
    private final HandoverRepository handoverRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping initialization");
            return;
        }

        log.info("Seeding database with initial data...");

        String encodedPassword = passwordEncoder.encode("password123");
        Instant now = Instant.now();

        // =====================================================================
        // 1. TEAMS
        // =====================================================================
        Team apmTeam = new Team();
        apmTeam.setName("APM Team");
        apmTeam.setDescription("Application Performance Monitoring Team");
        apmTeam.setCreatedAt(now);
        apmTeam = teamRepository.save(apmTeam);

        Team nocTeam = new Team();
        nocTeam.setName("NOC Team");
        nocTeam.setDescription("Network Operations Center Team");
        nocTeam.setCreatedAt(now);
        nocTeam = teamRepository.save(nocTeam);

        Team supportTeam = new Team();
        supportTeam.setName("SOC Team");
        supportTeam.setDescription("Security Operations Center Team");
        supportTeam.setCreatedAt(now);
        supportTeam = teamRepository.save(supportTeam);

        // =====================================================================
        // 2. USERS
        // =====================================================================
        User admin = User.builder()
                .email("admin123@example.com")
                .passwordHash(encodedPassword)
                .firstName("Admin")
                .lastName("User")
                .phone("+994501234567")
                .role(Role.ADMIN)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(30, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        admin = userRepository.save(admin);

        User aysel = User.builder()
                .email("aysel@example.com")
                .passwordHash(encodedPassword)
                .firstName("Aysel")
                .lastName("Quliyeva")
                .phone("+994501000001")
                .role(Role.SUPERVISOR)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(29, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        aysel = userRepository.save(aysel);

        User leyla = User.builder()
                .email("leyla@example.com")
                .passwordHash(encodedPassword)
                .firstName("Leyla")
                .lastName("Məmmədova")
                .phone("+994502345678")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(25, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        leyla = userRepository.save(leyla);

        User ali = User.builder()
                .email("ali@example.com")
                .passwordHash(encodedPassword)
                .firstName("Əli")
                .lastName("Həsənov")
                .phone("+994503456789")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(20, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        ali = userRepository.save(ali);

        User vuqar = User.builder()
                .email("vuqar@example.com")
                .passwordHash(encodedPassword)
                .firstName("Vüqar")
                .lastName("Rəhimov")
                .phone("+994504567890")
                .role(Role.SUPERVISOR)
                .isActive(true)
                .team(nocTeam)
                .createdAt(now.minus(28, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        vuqar = userRepository.save(vuqar);

        User nigar = User.builder()
                .email("nigar@example.com")
                .passwordHash(encodedPassword)
                .firstName("Nigar")
                .lastName("Əliyeva")
                .phone("+994505678901")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(nocTeam)
                .createdAt(now.minus(15, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        nigar = userRepository.save(nigar);

        User kamran = User.builder()
                .email("kamran@example.com")
                .passwordHash(encodedPassword)
                .firstName("Kamran")
                .lastName("Quliyev")
                .phone("+994506789012")
                .role(Role.SUPERVISOR)
                .isActive(true)
                .team(supportTeam)
                .createdAt(now.minus(22, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        kamran = userRepository.save(kamran);

        User murad = User.builder()
                .email("murad@example.com")
                .passwordHash(encodedPassword)
                .firstName("Murad")
                .lastName("Tağıyev")
                .phone("+994501000002")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(18, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        murad = userRepository.save(murad);

        User sabina = User.builder()
                .email("sabina@example.com")
                .passwordHash(encodedPassword)
                .firstName("Səbinə")
                .lastName("Nəsirova")
                .phone("+994501000003")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(17, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        sabina = userRepository.save(sabina);

        User orxan = User.builder()
                .email("orxan@example.com")
                .passwordHash(encodedPassword)
                .firstName("Orxan")
                .lastName("Əhmədov")
                .phone("+994501000004")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(nocTeam)
                .createdAt(now.minus(16, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        orxan = userRepository.save(orxan);

        User emil = User.builder()
                .email("emil@example.com")
                .passwordHash(encodedPassword)
                .firstName("Emil")
                .lastName("Rüstəmov")
                .phone("+994501000005")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(nocTeam)
                .createdAt(now.minus(14, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        emil = userRepository.save(emil);

        User sevda = User.builder()
                .email("sevda@example.com")
                .passwordHash(encodedPassword)
                .firstName("Sevda")
                .lastName("Məlikova")
                .phone("+994501000006")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(supportTeam)
                .createdAt(now.minus(13, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        sevda = userRepository.save(sevda);

        User rauf = User.builder()
                .email("rauf@example.com")
                .passwordHash(encodedPassword)
                .firstName("Rauf")
                .lastName("İsmayılov")
                .phone("+994501000007")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(supportTeam)
                .createdAt(now.minus(12, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        rauf = userRepository.save(rauf);

        User lala = User.builder()
                .email("lala@example.com")
                .passwordHash(encodedPassword)
                .firstName("Lalə")
                .lastName("Süleymanova")
                .phone("+994501000008")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(nocTeam)
                .createdAt(now.minus(11, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        lala = userRepository.save(lala);

        User elvin = User.builder()
                .email("elvin@example.com")
                .passwordHash(encodedPassword)
                .firstName("Elvin")
                .lastName("Qurbanov")
                .phone("+994501000009")
                .role(Role.EMPLOYEE)
                .isActive(true)
                .team(apmTeam)
                .createdAt(now.minus(10, ChronoUnit.DAYS))
                .updatedAt(now)
                .build();
        elvin = userRepository.save(elvin);

        // --- Set supervisors on teams ---
        apmTeam.setSupervisor(aysel);
        nocTeam.setSupervisor(vuqar);
        supportTeam.setSupervisor(kamran);
        teamRepository.save(apmTeam);
        teamRepository.save(nocTeam);
        teamRepository.save(supportTeam);

        // =====================================================================
        // 3. SHIFTS (12 shifts — various types & statuses, past/current/future)
        // =====================================================================

        // --- COMPLETED shifts (past) ---
        Shift shift1 = shiftRepository.save(Shift.builder()
                .user(leyla).team(apmTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(3, ChronoUnit.DAYS))
                .endTime(now.minus(3, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Normal day shift — all systems green")
                .createdAt(now.minus(4, ChronoUnit.DAYS))
                .build());

        Shift shift2 = shiftRepository.save(Shift.builder()
                .user(ali).team(apmTeam).shiftType(ShiftType.EVENING)
                .startTime(now.minus(3, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .endTime(now.minus(3, ChronoUnit.DAYS).plus(16, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Evening shift — handled 2 minor incidents")
                .createdAt(now.minus(4, ChronoUnit.DAYS))
                .build());

        Shift shift3 = shiftRepository.save(Shift.builder()
                .user(vuqar).team(nocTeam).shiftType(ShiftType.NIGHT)
                .startTime(now.minus(2, ChronoUnit.DAYS).minus(8, ChronoUnit.HOURS))
                .endTime(now.minus(2, ChronoUnit.DAYS))
                .status(ShiftStatus.COMPLETED)
                .notes("Night shift — network maintenance completed")
                .createdAt(now.minus(3, ChronoUnit.DAYS))
                .build());

        Shift shift4 = shiftRepository.save(Shift.builder()
                .user(nigar).team(nocTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(2, ChronoUnit.DAYS))
                .endTime(now.minus(2, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Monitored post-maintenance stability")
                .createdAt(now.minus(3, ChronoUnit.DAYS))
                .build());

        Shift shift5 = shiftRepository.save(Shift.builder()
                .user(kamran).team(supportTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(1, ChronoUnit.DAYS))
                .endTime(now.minus(1, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Support queue cleared — 15 tickets resolved")
                .createdAt(now.minus(2, ChronoUnit.DAYS))
                .build());

        // --- ACTIVE shifts (currently ongoing) ---
        Shift shift6 = shiftRepository.save(Shift.builder()
                .user(leyla).team(apmTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(2, ChronoUnit.HOURS))
                .endTime(now.plus(6, ChronoUnit.HOURS))
                .status(ShiftStatus.ACTIVE)
                .notes("APM monitoring — current shift")
                .createdAt(now.minus(1, ChronoUnit.DAYS))
                .build());

        Shift shift7 = shiftRepository.save(Shift.builder()
                .user(vuqar).team(nocTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(3, ChronoUnit.HOURS))
                .endTime(now.plus(5, ChronoUnit.HOURS))
                .status(ShiftStatus.ACTIVE)
                .notes("NOC monitoring — current shift")
                .createdAt(now.minus(1, ChronoUnit.DAYS))
                .build());

        Shift shift8 = shiftRepository.save(Shift.builder()
                .user(kamran).team(supportTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(1, ChronoUnit.HOURS))
                .endTime(now.plus(7, ChronoUnit.HOURS))
                .status(ShiftStatus.ACTIVE)
                .notes("Support desk — active")
                .createdAt(now.minus(1, ChronoUnit.DAYS))
                .build());

        // --- SCHEDULED shifts (future) ---
        Shift shift9 = shiftRepository.save(Shift.builder()
                .user(ali).team(apmTeam).shiftType(ShiftType.EVENING)
                .startTime(now.plus(6, ChronoUnit.HOURS))
                .endTime(now.plus(14, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Upcoming evening shift")
                .createdAt(now)
                .build());

        Shift shift10 = shiftRepository.save(Shift.builder()
                .user(nigar).team(nocTeam).shiftType(ShiftType.NIGHT)
                .startTime(now.plus(8, ChronoUnit.HOURS))
                .endTime(now.plus(16, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Upcoming night shift")
                .createdAt(now)
                .build());

        Shift shift11 = shiftRepository.save(Shift.builder()
                .user(leyla).team(apmTeam).shiftType(ShiftType.DAY)
                .startTime(now.plus(1, ChronoUnit.DAYS))
                .endTime(now.plus(1, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Tomorrow's day shift")
                .createdAt(now)
                .build());

        // --- CANCELLED shift ---
        Shift shift12 = shiftRepository.save(Shift.builder()
                .user(ali).team(apmTeam).shiftType(ShiftType.NIGHT)
                .startTime(now.plus(2, ChronoUnit.DAYS))
                .endTime(now.plus(2, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.CANCELLED)
                .notes("Cancelled due to schedule change")
                .createdAt(now)
                .build());

        Shift shift13 = shiftRepository.save(Shift.builder()
                .user(murad).team(apmTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(4, ChronoUnit.DAYS))
                .endTime(now.minus(4, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Handled APM alert tuning tasks")
                .createdAt(now.minus(5, ChronoUnit.DAYS))
                .build());

        Shift shift14 = shiftRepository.save(Shift.builder()
                .user(sabina).team(apmTeam).shiftType(ShiftType.EVENING)
                .startTime(now.minus(2, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS))
                .endTime(now.minus(1, ChronoUnit.DAYS))
                .status(ShiftStatus.COMPLETED)
                .notes("Evening monitoring and ticket triage")
                .createdAt(now.minus(3, ChronoUnit.DAYS))
                .build());

        Shift shift15 = shiftRepository.save(Shift.builder()
                .user(orxan).team(nocTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(90, ChronoUnit.MINUTES))
                .endTime(now.plus(390, ChronoUnit.MINUTES))
                .status(ShiftStatus.ACTIVE)
                .notes("Core link and router monitoring")
                .createdAt(now.minus(1, ChronoUnit.DAYS))
                .build());

        Shift shift16 = shiftRepository.save(Shift.builder()
                .user(emil).team(nocTeam).shiftType(ShiftType.EVENING)
                .startTime(now.plus(4, ChronoUnit.HOURS))
                .endTime(now.plus(12, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Planned evening operations shift")
                .createdAt(now)
                .build());

        Shift shift17 = shiftRepository.save(Shift.builder()
                .user(sevda).team(supportTeam).shiftType(ShiftType.DAY)
                .startTime(now.plus(1, ChronoUnit.DAYS))
                .endTime(now.plus(1, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Support queue coverage")
                .createdAt(now)
                .build());

        Shift shift18 = shiftRepository.save(Shift.builder()
                .user(rauf).team(supportTeam).shiftType(ShiftType.NIGHT)
                .startTime(now.plus(36, ChronoUnit.HOURS))
                .endTime(now.plus(44, ChronoUnit.HOURS))
                .status(ShiftStatus.SCHEDULED)
                .notes("Night support rotation")
                .createdAt(now)
                .build());

        Shift shift19 = shiftRepository.save(Shift.builder()
                .user(lala).team(nocTeam).shiftType(ShiftType.NIGHT)
                .startTime(now.plus(3, ChronoUnit.DAYS))
                .endTime(now.plus(3, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.CANCELLED)
                .notes("Cancelled due to urgent training")
                .createdAt(now)
                .build());

        Shift shift20 = shiftRepository.save(Shift.builder()
                .user(elvin).team(apmTeam).shiftType(ShiftType.DAY)
                .startTime(now.minus(5, ChronoUnit.DAYS))
                .endTime(now.minus(5, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS))
                .status(ShiftStatus.COMPLETED)
                .notes("Dashboard cleanup and performance checks")
                .createdAt(now.minus(6, ChronoUnit.DAYS))
                .build());

        // =====================================================================
        // 4. CHECKINS (6 — various statuses)
        // =====================================================================

        // CHECKED_IN + CHECKED_OUT (completed shift)
        checkinRepository.save(Checkin.builder()
                .shift(shift1).user(leyla)
                .checkInTime(shift1.getStartTime().plus(5, ChronoUnit.MINUTES))
                .checkOutTime(shift1.getEndTime().minus(5, ChronoUnit.MINUTES))
                .checkInNote("Reporting for day shift — all systems nominal")
                .checkOutNote("Shift completed — no outstanding issues")
                .status(CheckinStatus.CHECKED_OUT)
                .createdAt(shift1.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift2).user(ali)
                .checkInTime(shift2.getStartTime().plus(2, ChronoUnit.MINUTES))
                .checkOutTime(shift2.getEndTime().minus(3, ChronoUnit.MINUTES))
                .checkInNote("Evening shift started — taking over from Leyla")
                .checkOutNote("Two minor incidents documented and resolved")
                .status(CheckinStatus.CHECKED_OUT)
                .createdAt(shift2.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift3).user(vuqar)
                .checkInTime(shift3.getStartTime().plus(10, ChronoUnit.MINUTES))
                .checkOutTime(shift3.getEndTime().minus(2, ChronoUnit.MINUTES))
                .checkInNote("Night shift — starting network maintenance window")
                .checkOutNote("Maintenance completed successfully")
                .status(CheckinStatus.CHECKED_OUT)
                .createdAt(shift3.getStartTime())
                .build());

        // CHECKED_IN (active shift — not yet checked out)
        checkinRepository.save(Checkin.builder()
                .shift(shift6).user(leyla)
                .checkInTime(shift6.getStartTime().plus(3, ChronoUnit.MINUTES))
                .checkInNote("On shift — monitoring APM dashboards")
                .status(CheckinStatus.CHECKED_IN)
                .createdAt(shift6.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift7).user(vuqar)
                .checkInTime(shift7.getStartTime().plus(1, ChronoUnit.MINUTES))
                .checkInNote("NOC shift started — all network links up")
                .status(CheckinStatus.CHECKED_IN)
                .createdAt(shift7.getStartTime())
                .build());

        // MISSED checkin
        checkinRepository.save(Checkin.builder()
                .shift(shift4).user(nigar)
                .status(CheckinStatus.MISSED)
                .createdAt(shift4.getStartTime().plus(30, ChronoUnit.MINUTES))
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift13).user(murad)
                .checkInTime(shift13.getStartTime().plus(4, ChronoUnit.MINUTES))
                .checkOutTime(shift13.getEndTime().minus(6, ChronoUnit.MINUTES))
                .checkInNote("Morning APM checks completed")
                .checkOutNote("All alerts resolved before handover")
                .status(CheckinStatus.CHECKED_OUT)
                .createdAt(shift13.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift14).user(sabina)
                .checkInTime(shift14.getStartTime().plus(6, ChronoUnit.MINUTES))
                .checkOutTime(shift14.getEndTime().minus(4, ChronoUnit.MINUTES))
                .checkInNote("Evening shift takeover complete")
                .checkOutNote("Ticket queue stabilized")
                .status(CheckinStatus.CHECKED_OUT)
                .createdAt(shift14.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift15).user(orxan)
                .checkInTime(shift15.getStartTime().plus(2, ChronoUnit.MINUTES))
                .checkInNote("NOC active shift started")
                .status(CheckinStatus.CHECKED_IN)
                .createdAt(shift15.getStartTime())
                .build());

        checkinRepository.save(Checkin.builder()
                .shift(shift20).user(elvin)
                .status(CheckinStatus.MISSED)
                .createdAt(shift20.getStartTime().plus(25, ChronoUnit.MINUTES))
                .build());

        // =====================================================================
        // 5. SHIFT NOTES (8 — attached to various shifts)
        // =====================================================================
        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift1).user(leyla)
                .content(
                        "CPU usage on prod-server-01 spiked to 85% at 10:30. Investigated — caused by scheduled backup job. No action needed.")
                .createdAt(shift1.getStartTime().plus(2, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift1).user(leyla)
                .content("Deployed hotfix v2.3.1 to staging environment. All tests passed.")
                .createdAt(shift1.getStartTime().plus(5, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift2).user(ali)
                .content(
                        "Incident #1042: Database connection pool exhaustion on app-server-02. Increased pool size from 20 to 40. Monitoring.")
                .createdAt(shift2.getStartTime().plus(1, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift2).user(ali)
                .content("Incident #1042 resolved — connection pool stable after adjustment. No data loss.")
                .createdAt(shift2.getStartTime().plus(3, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift3).user(vuqar)
                .content("Starting scheduled maintenance on core-switch-01. Expected downtime: 30 minutes.")
                .createdAt(shift3.getStartTime().plus(1, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift3).user(vuqar)
                .content("Maintenance on core-switch-01 completed. All routes restored. Latency back to normal levels.")
                .createdAt(shift3.getStartTime().plus(2, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift6).user(leyla)
                .content("Memory alert on cache-server-03. Currently at 90%. Running diagnostics.")
                .createdAt(now.minus(1, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift7).user(vuqar)
                .content("Firewall rule update deployed to edge routers. Monitoring traffic patterns.")
                .createdAt(now.minus(30, ChronoUnit.MINUTES))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift13).user(murad)
                .content("Updated APM alert thresholds for JVM memory and GC pause time.")
                .createdAt(shift13.getStartTime().plus(2, ChronoUnit.HOURS))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift14).user(sabina)
                .content("Resolved backlog in evening queue and assigned two follow-up tasks.")
                .createdAt(shift14.getStartTime().plus(90, ChronoUnit.MINUTES))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift15).user(orxan)
                .content("Packet loss alert investigated on uplink-2; issue stabilized after interface reset.")
                .createdAt(now.minus(40, ChronoUnit.MINUTES))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift16).user(emil)
                .content("Prepared checklist for evening maintenance tasks.")
                .createdAt(now.plus(30, ChronoUnit.MINUTES))
                .build());

        shiftNoteRepository.save(ShiftNote.builder()
                .shift(shift17).user(sevda)
                .content("Documented top recurring support cases for weekly review.")
                .createdAt(now.plus(2, ChronoUnit.HOURS))
                .build());

        // =====================================================================
        // 6. SHIFT CHANGE REQUESTS (4 — various statuses)
        // =====================================================================
        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift9).user(ali)
                .reason("Family emergency — need to swap evening shift to day shift tomorrow")
                .requestedDate(LocalDate.now().plusDays(1))
                .status(ShiftChangeRequestStatus.PENDING)
                .createdAt(now.minus(1, ChronoUnit.HOURS))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift10).user(nigar)
                .reason("Medical appointment on Thursday evening — requesting shift swap")
                .requestedDate(LocalDate.now().plusDays(2))
                .status(ShiftChangeRequestStatus.PENDING)
                .createdAt(now.minus(2, ChronoUnit.HOURS))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift5).user(kamran)
                .reason("Conference attendance — shift was covered by a colleague")
                .requestedDate(LocalDate.now().minusDays(1))
                .status(ShiftChangeRequestStatus.APPROVED)
                .reviewedBy(admin)
                .reviewedAt(now.minus(12, ChronoUnit.HOURS))
                .createdAt(now.minus(2, ChronoUnit.DAYS))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift12).user(ali)
                .reason("Personal preference — want to move to day shift instead")
                .requestedDate(LocalDate.now().plusDays(2))
                .status(ShiftChangeRequestStatus.REJECTED)
                .reviewedBy(admin)
                .reviewedAt(now.minus(6, ChronoUnit.HOURS))
                .createdAt(now.minus(1, ChronoUnit.DAYS))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift16).user(emil)
                .reason("Evening shift conflicts with university exam schedule")
                .requestedDate(LocalDate.now().plusDays(2))
                .status(ShiftChangeRequestStatus.PENDING)
                .createdAt(now.minus(45, ChronoUnit.MINUTES))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift17).user(sevda)
                .reason("Family event on planned day shift date")
                .requestedDate(LocalDate.now().plusDays(3))
                .status(ShiftChangeRequestStatus.APPROVED)
                .reviewedBy(kamran)
                .reviewedAt(now.minus(20, ChronoUnit.MINUTES))
                .createdAt(now.minus(8, ChronoUnit.HOURS))
                .build());

        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(shift18).user(rauf)
                .reason("Night transport issue, requesting replacement")
                .requestedDate(LocalDate.now().plusDays(3))
                .status(ShiftChangeRequestStatus.REJECTED)
                .reviewedBy(aysel)
                .reviewedAt(now.minus(15, ChronoUnit.MINUTES))
                .createdAt(now.minus(7, ChronoUnit.HOURS))
                .build());

        // =====================================================================
        // 7. HANDOVERS (6 — various statuses)
        // =====================================================================

        // APPROVED handover (shift1 → shift2: Leyla -> Ali)
        handoverRepository.save(Handover.builder()
                .shift(shift1).fromUser(leyla).toUser(ali)
                .incidents(
                        "No critical incidents during shift. CPU spike on prod-server-01 was caused by scheduled backup — self-resolved.")
                .systemStatus("All systems operational. CPU: 45%, Memory: 62%, Disk: 71%. All health checks passing.")
                .pendingTasks("Monitor hotfix v2.3.1 deployment on staging. Push to production if stable for 2 hours.")
                .nextShiftInfo(
                        "Evening shift 16:00-00:00. Ali Həsənov taking over. Focus on staging deployment validation.")
                .additionalNotes("Backup job schedule updated to run at 03:00 instead of 10:00 to avoid peak hours.")
                .status(HandoverStatus.APPROVED)
                .submittedAt(shift1.getEndTime().minus(30, ChronoUnit.MINUTES))
                .approvedAt(shift1.getEndTime().minus(10, ChronoUnit.MINUTES))
                .approvedBy(admin)
                .createdAt(shift1.getEndTime().minus(1, ChronoUnit.HOURS))
                .build());

        // APPROVED handover (shift3 → shift4: Vuqar -> Nigar)
        handoverRepository.save(Handover.builder()
                .shift(shift3).fromUser(vuqar).toUser(nigar)
                .incidents(
                        "Scheduled maintenance on core-switch-01 completed. 30 minute downtime window used. All routes restored.")
                .systemStatus(
                        "Network operational. Latency: 12ms avg. Bandwidth utilization: 35%. No packet loss detected.")
                .pendingTasks(
                        "Monitor post-maintenance stability for 24 hours. Check core-switch-01 logs every 2 hours.")
                .nextShiftInfo("Day shift 08:00-16:00. Nigar Əliyeva monitoring. Focus on post-maintenance validation.")
                .additionalNotes(
                        "Firmware update for edge-router-02 scheduled for next maintenance window (Friday 02:00).")
                .status(HandoverStatus.APPROVED)
                .submittedAt(shift3.getEndTime().minus(20, ChronoUnit.MINUTES))
                .approvedAt(shift3.getEndTime().minus(5, ChronoUnit.MINUTES))
                .approvedBy(vuqar)
                .createdAt(shift3.getEndTime().minus(45, ChronoUnit.MINUTES))
                .build());

        // SUBMITTED handover (shift5: Kamran — waiting for approval)
        handoverRepository.save(Handover.builder()
                .shift(shift5).fromUser(kamran)
                .incidents("No incidents. 15 support tickets resolved. Average response time: 8 minutes.")
                .systemStatus(
                        "Helpdesk system operational. Ticket queue empty. Knowledge base updated with 3 new articles.")
                .pendingTasks("2 tickets escalated to engineering team — awaiting their response. Follow up tomorrow.")
                .nextShiftInfo("Next support shift tomorrow 08:00. Queue expected to be light.")
                .additionalNotes("New support agent onboarding guide updated in Confluence.")
                .status(HandoverStatus.SUBMITTED)
                .submittedAt(shift5.getEndTime().minus(15, ChronoUnit.MINUTES))
                .createdAt(shift5.getEndTime().minus(1, ChronoUnit.HOURS))
                .build());

        // DRAFT handover (shift6: Leyla — currently writing, active shift)
        handoverRepository.save(Handover.builder()
                .shift(shift6).fromUser(leyla)
                .incidents("Memory alert on cache-server-03. Currently investigating. No impact on end users yet.")
                .systemStatus("Mostly operational. cache-server-03 memory at 90% — diagnostics running.")
                .pendingTasks("Complete cache-server-03 diagnostics. May need to restart the service.")
                .nextShiftInfo("To be determined — shift still in progress.")
                .status(HandoverStatus.DRAFT)
                .createdAt(now.minus(30, ChronoUnit.MINUTES))
                .build());

        // REJECTED handover (shift2: Ali — was rejected because it was incomplete)
        handoverRepository.save(Handover.builder()
                .shift(shift2).fromUser(ali).toUser(vuqar)
                .incidents("DB connection pool issue.")
                .systemStatus("OK")
                .pendingTasks("")
                .nextShiftInfo("Night shift")
                .additionalNotes("Needs more detail about the incident resolution.")
                .status(HandoverStatus.REJECTED)
                .submittedAt(shift2.getEndTime().minus(10, ChronoUnit.MINUTES))
                .approvedAt(shift2.getEndTime().plus(5, ChronoUnit.MINUTES))
                .approvedBy(admin)
                .createdAt(shift2.getEndTime().minus(30, ChronoUnit.MINUTES))
                .build());

        // Another SUBMITTED handover (shift7: Vuqar — active but pre-writing)
        handoverRepository.save(Handover.builder()
                .shift(shift7).fromUser(vuqar).toUser(nigar)
                .incidents("Firewall rule update deployed to edge routers. No incidents so far.")
                .systemStatus("All network links operational. Traffic patterns normal after firewall update.")
                .pendingTasks("Continue monitoring traffic after firewall changes for anomalies.")
                .nextShiftInfo("Night shift — Nigar Əliyeva. Focus on traffic pattern analysis.")
                .additionalNotes("New firewall rules documented in network-config repo.")
                .status(HandoverStatus.SUBMITTED)
                .submittedAt(now.minus(15, ChronoUnit.MINUTES))
                .createdAt(now.minus(45, ChronoUnit.MINUTES))
                .build());

        handoverRepository.save(Handover.builder()
                .shift(shift13).fromUser(murad).toUser(sabina)
                .incidents("Alert noise reduced after threshold updates.")
                .systemStatus("APM dashboards stable and all collectors online.")
                .pendingTasks("Validate new alerts during evening traffic spike.")
                .nextShiftInfo("Sabina takes over evening monitoring and backlog triage.")
                .additionalNotes("New runbook version shared in team channel.")
                .status(HandoverStatus.APPROVED)
                .submittedAt(shift13.getEndTime().minus(20, ChronoUnit.MINUTES))
                .approvedAt(shift13.getEndTime().minus(5, ChronoUnit.MINUTES))
                .approvedBy(aysel)
                .createdAt(shift13.getEndTime().minus(50, ChronoUnit.MINUTES))
                .build());

        handoverRepository.save(Handover.builder()
                .shift(shift14).fromUser(sabina).toUser(leyla)
                .incidents("Ticket queue and alert stream normalized.")
                .systemStatus("No high severity incidents open.")
                .pendingTasks("Recheck two medium tickets in the morning.")
                .nextShiftInfo("Leyla to continue monitoring daytime workload.")
                .additionalNotes("All handoff documents attached in shift notes.")
                .status(HandoverStatus.SUBMITTED)
                .submittedAt(shift14.getEndTime().minus(18, ChronoUnit.MINUTES))
                .createdAt(shift14.getEndTime().minus(45, ChronoUnit.MINUTES))
                .build());

        handoverRepository.save(Handover.builder()
                .shift(shift15).fromUser(orxan).toUser(emil)
                .incidents("Transient packet loss incident mitigated.")
                .systemStatus("Backbone network stable after interface reset.")
                .pendingTasks("Monitor uplink-2 every 30 minutes.")
                .nextShiftInfo("Emil handles evening continuity checks.")
                .additionalNotes("Escalation contact list refreshed.")
                .status(HandoverStatus.DRAFT)
                .createdAt(now.minus(10, ChronoUnit.MINUTES))
                .build());

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(admin)
                .token("111111")
                .expiresAt(now.plus(90, ChronoUnit.MINUTES))
                .isUsed(false)
                .createdAt(now.minus(30, ChronoUnit.MINUTES))
                .build());

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(leyla)
                .token("222222")
                .expiresAt(now.plus(60, ChronoUnit.MINUTES))
                .isUsed(false)
                .createdAt(now.minus(20, ChronoUnit.MINUTES))
                .build());

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(ali)
                .token("333333")
                .expiresAt(now.minus(10, ChronoUnit.MINUTES))
                .isUsed(false)
                .createdAt(now.minus(3, ChronoUnit.HOURS))
                .build());

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(vuqar)
                .token("444444")
                .expiresAt(now.plus(120, ChronoUnit.MINUTES))
                .isUsed(true)
                .createdAt(now.minus(4, ChronoUnit.HOURS))
                .build());

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(murad)
                .token("555555")
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .isUsed(false)
                .createdAt(now.minus(5, ChronoUnit.MINUTES))
                .build());

        // =====================================================================
        // LOGGING
        // =====================================================================
        log.info("Database seeded successfully!");
        log.info("  Teams: APM Team, NOC Team, Support Team");
        log.info("  Users:");
        log.info("    admin123@example.com / password123 (ADMIN)");
        log.info("    aysel@example.com    / password123 (SUPERVISOR)");
        log.info("    vuqar@example.com    / password123 (SUPERVISOR)");
        log.info("    kamran@example.com   / password123 (SUPERVISOR)");
        log.info("    leyla@example.com    / password123 (EMPLOYEE)");
        log.info("    ali@example.com      / password123 (EMPLOYEE)");
        log.info("    nigar@example.com    / password123 (EMPLOYEE)");
        log.info("    murad@example.com    / password123 (EMPLOYEE)");
        log.info("    sabina@example.com   / password123 (EMPLOYEE)");
        log.info("    orxan@example.com    / password123 (EMPLOYEE)");
        log.info("    emil@example.com     / password123 (EMPLOYEE)");
        log.info("    sevda@example.com    / password123 (EMPLOYEE)");
        log.info("    rauf@example.com     / password123 (EMPLOYEE)");
        log.info("    lala@example.com     / password123 (EMPLOYEE)");
        log.info("    elvin@example.com    / password123 (EMPLOYEE)");
        log.info("  Shifts: 20 (8 COMPLETED, 4 ACTIVE, 6 SCHEDULED, 2 CANCELLED)");
        log.info("  Checkins: 10 (5 CHECKED_OUT, 3 CHECKED_IN, 2 MISSED)");
        log.info("  Shift Notes: 13");
        log.info("  Shift Change Requests: 7 (3 PENDING, 2 APPROVED, 2 REJECTED)");
        log.info("  Handovers: 9 (3 APPROVED, 3 SUBMITTED, 2 DRAFT, 1 REJECTED)");
        log.info("  Password Reset Tokens: 5");
    }
}
