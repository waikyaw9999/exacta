package com.exacta.timer.config;

import com.exacta.timer.entity.Client;
import com.exacta.timer.entity.Project;
import com.exacta.timer.entity.ProjectStatus;
import com.exacta.timer.entity.Role;
import com.exacta.timer.entity.TimeEntry;
import com.exacta.timer.entity.TimeEntryStatus;
import com.exacta.timer.entity.User;
import com.exacta.timer.repository.ClientRepository;
import com.exacta.timer.repository.ProjectRepository;
import com.exacta.timer.repository.TimeEntryRepository;
import com.exacta.timer.repository.UserRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private final SeedProperties seedProperties;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("Skipping demo seed; users already exist");
            return;
        }

        String passwordHash = passwordEncoder.encode(seedProperties.getAdminPassword());

        User ada = userRepository.save(User.builder()
                .name("Ada Chen")
                .email(seedProperties.getAdminEmail().trim().toLowerCase())
                .password(passwordHash)
                .role(Role.ADMIN)
                .hourlyRate(new BigDecimal("350.00"))
                .build());
        User marcus = userRepository.save(User.builder()
                .name("Marcus Hale")
                .email("marcus@exacta.test")
                .password(passwordEncoder.encode(seedProperties.getMemberPassword()))
                .role(Role.MEMBER)
                .hourlyRate(new BigDecimal("225.00"))
                .build());
        User priya = userRepository.save(User.builder()
                .name("Priya Shah")
                .email("priya@exacta.test")
                .password(passwordEncoder.encode(seedProperties.getMemberPassword()))
                .role(Role.MEMBER)
                .hourlyRate(new BigDecimal("185.00"))
                .build());

        Client northwind = clientRepository.save(Client.builder()
                .name("Northwind Legal")
                .contactEmail("billing@northwind.test")
                .company("Northwind LLP")
                .build());
        Client apex = clientRepository.save(Client.builder()
                .name("Apex Advisory")
                .contactEmail("accounts@apex.test")
                .company("Apex Advisory Group")
                .build());
        Client harbor = clientRepository.save(Client.builder()
                .name("Harbor Partners")
                .contactEmail("finance@harbor.test")
                .company("Harbor Partners PC")
                .build());

        Project merger = projectRepository.save(Project.builder()
                .name("Merger diligence")
                .client(northwind)
                .status(ProjectStatus.ACTIVE)
                .build());
        Project contracts = projectRepository.save(Project.builder()
                .name("Contract review — Q3")
                .client(northwind)
                .status(ProjectStatus.ACTIVE)
                .build());
        Project retainer = projectRepository.save(Project.builder()
                .name("Retainer — general counsel")
                .client(apex)
                .status(ProjectStatus.ACTIVE)
                .build());
        Project training = projectRepository.save(Project.builder()
                .name("Internal training (non-billable)")
                .client(apex)
                .status(ProjectStatus.ACTIVE)
                .build());
        Project litigation = projectRepository.save(Project.builder()
                .name("Litigation support — Chen")
                .client(harbor)
                .status(ProjectStatus.ACTIVE)
                .build());

        timeEntryRepository.save(entry(ada, merger, 0, 9, 150, true, TimeEntryStatus.STOPPED, "SPA markup and reps schedule"));
        timeEntryRepository.save(entry(marcus, litigation, 0, 13, 90, true, TimeEntryStatus.SUBMITTED, "Deposition outline"));
        timeEntryRepository.save(entry(priya, training, 0, 16, 45, false, TimeEntryStatus.STOPPED, "Associate onboarding session"));
        timeEntryRepository.save(entry(ada, contracts, 1, 10, 180, true, TimeEntryStatus.SUBMITTED, "Vendor MSA redlines"));
        timeEntryRepository.save(entry(marcus, retainer, 1, 14, 120, true, TimeEntryStatus.STOPPED, "Board consent questions"));
        timeEntryRepository.save(entry(priya, merger, 2, 8, 210, true, TimeEntryStatus.STOPPED, "IP assignment chain"));
        timeEntryRepository.save(entry(ada, training, 2, 15, 60, false, TimeEntryStatus.STOPPED, "Knowledge share: billing hygiene"));
        timeEntryRepository.save(entry(marcus, litigation, 3, 11, 75, true, TimeEntryStatus.BILLED, "Privilege log — already invoiced"));
        timeEntryRepository.save(entry(priya, retainer, 3, 9, 95, true, TimeEntryStatus.SUBMITTED, "Employment handbook review"));
        timeEntryRepository.save(entry(ada, litigation, 4, 10, 240, true, TimeEntryStatus.STOPPED, "Hearing prep memo"));
        timeEntryRepository.save(entry(marcus, contracts, 4, 15, 40, false, TimeEntryStatus.STOPPED, "Internal conflicts check"));
        timeEntryRepository.save(entry(priya, retainer, -3, 9, 180, true, TimeEntryStatus.STOPPED, "Prior-week work"));

        log.info("Seeded demo data. Admin login: {}", seedProperties.getAdminEmail());
    }

    private TimeEntry entry(
            User user,
            Project project,
            int dayOffset,
            int hour,
            int durationMinutes,
            boolean billable,
            TimeEntryStatus status,
            String description) {
        Instant start = atWeekDay(dayOffset, hour);
        Instant end = start.plusSeconds(durationMinutes * 60L);
        return TimeEntry.builder()
                .user(user)
                .project(project)
                .startTime(start)
                .endTime(end)
                .durationMinutes(durationMinutes)
                .description(description)
                .billable(billable)
                .status(status)
                .build();
    }

    private Instant atWeekDay(int dayOffset, int hour) {
        LocalDate monday = LocalDate.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY);
        LocalDateTime dateTime = monday.plusDays(dayOffset).atTime(hour, 0);
        return dateTime.toInstant(ZoneOffset.UTC);
    }
}
