package com.exacta.timer.service;

import com.exacta.timer.dto.timeentry.StartTimerRequest;
import com.exacta.timer.dto.timeentry.TimeEntryRequest;
import com.exacta.timer.dto.timeentry.TimeEntryResponse;
import com.exacta.timer.entity.Project;
import com.exacta.timer.entity.TimeEntry;
import com.exacta.timer.entity.TimeEntryStatus;
import com.exacta.timer.entity.User;
import com.exacta.timer.exception.ConflictException;
import com.exacta.timer.exception.InvalidRequestException;
import com.exacta.timer.exception.ResourceNotFoundException;
import com.exacta.timer.repository.TimeEntryRepository;
import com.exacta.timer.security.UserPrincipal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserService userService;
    private final ProjectService projectService;

    public List<TimeEntryResponse> findAll(Long projectId, TimeEntryStatus status, UserPrincipal actor) {
        Long userId = actor.isAdmin() ? null : actor.id();
        return timeEntryRepository.search(userId, projectId, status).stream()
                .map(this::toResponse)
                .toList();
    }

    public TimeEntryResponse findById(Long id, UserPrincipal actor) {
        TimeEntry entry = getByIdWithRelations(id);
        requireOwnerOrAdmin(entry, actor);
        return toResponse(entry);
    }

    public TimeEntryResponse findActive(UserPrincipal actor) {
        return timeEntryRepository
                .findByUserIdAndStatusWithRelations(actor.id(), TimeEntryStatus.RUNNING)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public TimeEntryResponse create(TimeEntryRequest request, UserPrincipal actor) {
        Long ownerId = resolveOwnerId(request.userId(), actor);
        User user = userService.getById(ownerId);
        Project project = projectService.getByIdWithClient(request.projectId());
        TimeEntryStatus status = resolveCreateStatus(request);

        if (status == TimeEntryStatus.RUNNING) {
            ensureNoRunningTimer(ownerId);
        }

        Instant endTime = request.endTime();
        TimeEntry saved = timeEntryRepository.save(TimeEntry.builder()
                .user(user)
                .project(project)
                .startTime(request.startTime())
                .endTime(endTime)
                .durationMinutes(computeDuration(request.startTime(), endTime))
                .description(normalizeDescription(request.description()))
                .billable(Boolean.TRUE.equals(request.isBillable()))
                .status(status)
                .build());
        log.info("Created time entry {} for user {}", saved.getId(), ownerId);
        return toResponse(getByIdWithRelations(saved.getId()));
    }

    @Transactional
    public TimeEntryResponse start(StartTimerRequest request, UserPrincipal actor) {
        ensureNoRunningTimer(actor.id());
        User user = userService.getById(actor.id());
        Project project = projectService.getByIdWithClient(request.projectId());
        Instant now = Instant.now();

        TimeEntry saved = timeEntryRepository.save(TimeEntry.builder()
                .user(user)
                .project(project)
                .startTime(now)
                .endTime(null)
                .durationMinutes(0)
                .description(normalizeDescription(request.description()))
                .billable(request.isBillable() == null || request.isBillable())
                .status(TimeEntryStatus.RUNNING)
                .build());
        log.info("Started timer {} for user {}", saved.getId(), actor.id());
        return toResponse(getByIdWithRelations(saved.getId()));
    }

    @Transactional
    public TimeEntryResponse stop(Long id, UserPrincipal actor) {
        TimeEntry entry = getByIdWithRelations(id);
        requireOwnerOrAdmin(entry, actor);
        if (entry.getStatus() != TimeEntryStatus.RUNNING) {
            throw new InvalidRequestException("Only a running timer can be stopped");
        }

        Instant endTime = Instant.now();
        entry.setEndTime(endTime);
        entry.setDurationMinutes(computeDuration(entry.getStartTime(), endTime));
        entry.setStatus(TimeEntryStatus.STOPPED);
        log.info("Stopped timer {}", id);
        return toResponse(entry);
    }

    @Transactional
    public TimeEntryResponse update(Long id, TimeEntryRequest request, UserPrincipal actor) {
        TimeEntry entry = getByIdWithRelations(id);
        requireOwnerOrAdmin(entry, actor);
        ensureMutable(entry);

        Long ownerId = resolveOwnerId(request.userId(), actor);
        if (!entry.getUser().getId().equals(ownerId)) {
            requireAdmin(actor);
            entry.setUser(userService.getById(ownerId));
        }

        if (!entry.getProject().getId().equals(request.projectId())) {
            entry.setProject(projectService.getByIdWithClient(request.projectId()));
        }

        TimeEntryStatus nextStatus = request.status() != null ? request.status() : entry.getStatus();
        if (nextStatus == TimeEntryStatus.RUNNING && entry.getStatus() != TimeEntryStatus.RUNNING) {
            ensureNoRunningTimer(entry.getUser().getId());
        }

        Instant endTime = request.endTime();
        if (nextStatus == TimeEntryStatus.RUNNING) {
            endTime = null;
        }

        entry.setStartTime(request.startTime());
        entry.setEndTime(endTime);
        entry.setDurationMinutes(computeDuration(request.startTime(), endTime));
        entry.setDescription(normalizeDescription(request.description()));
        entry.setBillable(Boolean.TRUE.equals(request.isBillable()));
        entry.setStatus(nextStatus);
        return toResponse(entry);
    }

    @Transactional
    public void delete(Long id, UserPrincipal actor) {
        TimeEntry entry = getByIdWithRelations(id);
        requireOwnerOrAdmin(entry, actor);
        ensureMutable(entry);
        timeEntryRepository.delete(entry);
        log.info("Deleted time entry {}", id);
    }

    private TimeEntry getByIdWithRelations(Long id) {
        return timeEntryRepository
                .findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time entry not found: " + id));
    }

    private TimeEntryStatus resolveCreateStatus(TimeEntryRequest request) {
        if (request.status() != null) {
            return request.status();
        }
        return request.endTime() == null ? TimeEntryStatus.RUNNING : TimeEntryStatus.STOPPED;
    }

    private Long resolveOwnerId(Long requestedUserId, UserPrincipal actor) {
        if (requestedUserId == null || requestedUserId.equals(actor.id())) {
            return actor.id();
        }
        requireAdmin(actor);
        return requestedUserId;
    }

    private void ensureNoRunningTimer(Long userId) {
        if (timeEntryRepository.existsByUserIdAndStatus(userId, TimeEntryStatus.RUNNING)) {
            throw new ConflictException("A timer is already running for this user");
        }
    }

    private void ensureMutable(TimeEntry entry) {
        if (entry.getStatus() == TimeEntryStatus.BILLED) {
            throw new ConflictException("Billed time entries cannot be modified");
        }
    }

    private int computeDuration(Instant startTime, Instant endTime) {
        if (endTime == null) {
            return 0;
        }
        if (endTime.isBefore(startTime)) {
            throw new InvalidRequestException("endTime must be on or after startTime");
        }
        return Math.toIntExact(Duration.between(startTime, endTime).toMinutes());
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }

    private void requireOwnerOrAdmin(TimeEntry entry, UserPrincipal actor) {
        if (!actor.isAdmin() && !entry.getUser().getId().equals(actor.id())) {
            throw new AccessDeniedException("You can only access your own time entries");
        }
    }

    private void requireAdmin(UserPrincipal actor) {
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Admin role required");
        }
    }

    public TimeEntryResponse toResponse(TimeEntry entry) {
        return new TimeEntryResponse(
                entry.getId(),
                entry.getUser().getId(),
                entry.getUser().getName(),
                entry.getProject().getId(),
                entry.getProject().getName(),
                entry.getProject().getClient().getId(),
                entry.getProject().getClient().getName(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getDurationMinutes(),
                entry.getDescription(),
                entry.isBillable(),
                entry.getStatus());
    }
}
