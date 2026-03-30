package com.example.dutydesk.config;

import com.example.dutydesk.entities.*;
import com.example.dutydesk.enums.*;
import com.example.dutydesk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile({"legacy-seed"})
public class DataSeeder implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final CheckinRepository checkinRepository;
    private final ShiftNoteRepository shiftNoteRepository;
    private final ShiftChangeRequestRepository shiftChangeRequestRepository;
    private final HandoverRepository handoverRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedTeamsAndUsers();
        seedShiftsAndCheckinsAndNotes();
        seedHandovers();
    }

    private void seedTeamsAndUsers() {
        Team apm = ensureTeam("APM Team", "Application Performance Monitoring");
        Team noc = ensureTeam("NOC Team", "Network Operations Center");
        Team soc = ensureTeam("SOC Team", "Security Operations Center");

        User admin = ensureUser("admin123@example.com", "Admin", "User", Role.ADMIN, apm, "0500000001");
        User leyla = ensureUser("leyla@example.com", "Leyla", "Məmmədova", Role.EMPLOYEE, apm, "0500000002");
        User ali = ensureUser("ali@example.com", "Əli", "Həsənov", Role.EMPLOYEE, apm, "0500000003");
        User vuqar = ensureUser("vuqar@example.com", "Vüqar", "Rəhimov", Role.SUPERVISOR, noc, "0500000004");
        User ayşe = ensureUser("ayse@example.com", "Ayşe", "Yılmaz", Role.EMPLOYEE, soc, "0500000005");

        if (noc.getSupervisor() == null) {
            noc.setSupervisor(vuqar);
            teamRepository.save(noc);
        }
    }

    private Team ensureTeam(String name, String description) {
        List<Team> existing = teamRepository.findAll().stream().filter(t -> name.equalsIgnoreCase(t.getName())).toList();
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        return teamRepository.save(team);
    }

    private User ensureUser(String email, String firstName, String lastName, Role role, Team team, String phone) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .team(team)
                .phone(phone)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    private void seedShiftsAndCheckinsAndNotes() {
        // If there are already shifts, skip creating duplicates
        if (shiftRepository.count() > 0) {
            return;
        }

        User leyla = userRepository.findByEmail("leyla@example.com").orElseThrow();
        User ali = userRepository.findByEmail("ali@example.com").orElseThrow();
        User vuqar = userRepository.findByEmail("vuqar@example.com").orElseThrow();

        // Create a current active shift for Leyla (supports /current)
        Shift leylaCurrent = createShiftForDay(leyla, leyla.getTeam(), LocalDate.now(), ShiftType.DAY);
        // check-in Leyla
        Checkin lCheckin = Checkin.builder()
                .shift(leylaCurrent)
                .user(leyla)
                .checkInTime(Instant.now().minusSeconds(30 * 60))
                .checkInNote("Növbəyə başladım")
                .status(CheckinStatus.CHECKED_IN)
                .build();
        checkinRepository.save(lCheckin);
        leylaCurrent.setCheckin(lCheckin);
        leylaCurrent.setStatus(ShiftStatus.ACTIVE);
        shiftRepository.save(leylaCurrent);

        // Add some notes to Leyla's current shift
        shiftNoteRepository.save(ShiftNote.builder()
                .shift(leylaCurrent)
                .user(leyla)
                .content("Sistem yoxlaması tamamlandı")
                .createdAt(Instant.now().minusSeconds(20 * 60))
                .build());
        shiftNoteRepository.save(ShiftNote.builder()
                .shift(leylaCurrent)
                .user(leyla)
                .content("APM alertləri həll edildi")
                .createdAt(Instant.now().minusSeconds(10 * 60))
                .build());

        // Create yesterday completed shift for Leyla with check-out
        Shift leylaYesterday = createShiftForDay(leyla, leyla.getTeam(), LocalDate.now().minusDays(1), ShiftType.DAY);
        Checkin lCheckinYesterday = Checkin.builder()
                .shift(leylaYesterday)
                .user(leyla)
                .checkInTime(toInstant(LocalDate.now().minusDays(1), 8, 0))
                .checkOutTime(toInstant(LocalDate.now().minusDays(1), 16, 0))
                .checkInNote("Giriş edildi")
                .checkOutNote("Çıxış edildi")
                .status(CheckinStatus.CHECKED_OUT)
                .build();
        checkinRepository.save(lCheckinYesterday);
        leylaYesterday.setCheckin(lCheckinYesterday);
        leylaYesterday.setStatus(ShiftStatus.COMPLETED);
        shiftRepository.save(leylaYesterday);

        // Change request for Ali's shift
        Shift aliTomorrow = createShiftForDay(ali, ali.getTeam(), LocalDate.now().plusDays(1), ShiftType.EVENING);
        shiftChangeRequestRepository.save(ShiftChangeRequest.builder()
                .shift(aliTomorrow)
                .user(ali)
                .reason("Şəxsi səbəb")
                .requestedDate(LocalDate.now().plusDays(3))
                .status(ShiftChangeRequestStatus.PENDING)
                .build());

        // Several shifts across the week for schedule view
        createShiftForDay(ali, ali.getTeam(), mondayOfThisWeek(), ShiftType.DAY);
        createShiftForDay(ali, ali.getTeam(), mondayOfThisWeek().plusDays(2), ShiftType.NIGHT);
        createShiftForDay(leyla, leyla.getTeam(), mondayOfThisWeek().plusDays(1), ShiftType.EVENING);
        createShiftForDay(vuqar, vuqar.getTeam(), mondayOfThisWeek().plusDays(4), ShiftType.DAY);
        
        // SOC team shifts
        User ayşe = userRepository.findByEmail("ayse@example.com").orElseThrow();
        createShiftForDay(ayşe, ayşe.getTeam(), mondayOfThisWeek().plusDays(3), ShiftType.NIGHT);
        createShiftForDay(ayşe, ayşe.getTeam(), mondayOfThisWeek().plusDays(5), ShiftType.DAY);
    }

    private void seedHandovers() {
        // If there are already handovers, skip
        if (handoverRepository.count() > 0) {
            return;
        }
        User leyla = userRepository.findByEmail("leyla@example.com").orElseThrow();
        User ali = userRepository.findByEmail("ali@example.com").orElseThrow();
        User vuqar = userRepository.findByEmail("vuqar@example.com").orElseThrow();

        // Use Leyla's completed shift for submitted/approved handovers
        Shift completedShift = shiftRepository
                .findForUser("leyla@example.com", ShiftStatus.COMPLETED, Instant.EPOCH,
                        Instant.parse("9999-12-31T23:59:59Z"))
                .stream().findFirst().orElseGet(() ->
                        createShiftForDay(leyla, leyla.getTeam(), LocalDate.now().minusDays(2), ShiftType.DAY));

        // Draft handover
        handoverRepository.save(Handover.builder()
                .shift(completedShift)
                .fromUser(leyla)
                .toUser(ali)
                .incidents("Kiçik hadisələr baş verdi, loglar yoxlandı")
                .systemStatus("Bütün sistemlər stabil")
                .pendingTasks("Backup planı nəzərdən keçirilməlidir")
                .nextShiftInfo("APM dashboard izlənsin, saat 10:00-da yoxlama")
                .additionalNotes("Heç bir əlavə problem yoxdur")
                .status(HandoverStatus.DRAFT)
                .build());

        // Submitted handover
        handoverRepository.save(Handover.builder()
                .shift(completedShift)
                .fromUser(leyla)
                .toUser(ali)
                .incidents("DB bağlantısında qısa gecikmələr")
                .systemStatus("PostgreSQL replikası stabildir")
                .pendingTasks("Replika lag monitorinqi")
                .nextShiftInfo("Növbəti növbə DB performansına diqqət etsin")
                .additionalNotes("Grafana alertləri yeniləndi")
                .status(HandoverStatus.SUBMITTED)
                .submittedAt(Instant.now().minusSeconds(3600))
                .build());

        // Approved handover
        handoverRepository.save(Handover.builder()
                .shift(completedShift)
                .fromUser(leyla)
                .toUser(ali)
                .incidents("Şəbəkə cihazında firmware yeniləndi")
                .systemStatus("NOC avadanlıqları stabil")
                .pendingTasks("SNMP konfiqurasiya yoxlaması")
                .nextShiftInfo("SNMP trap-ları izləyin")
                .additionalNotes("Yeniləmə uğurla tamamlandı")
                .status(HandoverStatus.APPROVED)
                .submittedAt(Instant.now().minusSeconds(7200))
                .approvedAt(Instant.now().minusSeconds(3600))
                .approvedBy(vuqar)
                .build());
    }

    private Shift createShiftForDay(User user, Team team, LocalDate day, ShiftType type) {
        Instant start;
        Instant end;
        switch (type) {
            case DAY -> {
                start = toInstant(day, 8, 0);
                end = toInstant(day, 16, 0);
            }
            case EVENING -> {
                start = toInstant(day, 16, 0);
                end = toInstant(day.plusDays(1), 0, 0);
            }
            case NIGHT -> {
                start = toInstant(day, 0, 0);
                end = toInstant(day, 8, 0);
            }
            default -> throw new IllegalArgumentException("Unknown shift type");
        }
        Shift shift = Shift.builder()
                .user(user)
                .team(team)
                .shiftType(type)
                .startTime(start)
                .endTime(end)
                .status(ShiftStatus.SCHEDULED)
                .notes("Auto-seeded")
                .build();
        return shiftRepository.save(shift);
    }

    private Instant toInstant(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute).toInstant(ZoneOffset.UTC);
    }

    private LocalDate mondayOfThisWeek() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return today.with(java.time.DayOfWeek.MONDAY);
    }
}
