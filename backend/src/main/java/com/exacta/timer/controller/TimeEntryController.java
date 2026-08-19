package com.exacta.timer.controller;

import com.exacta.timer.dto.timeentry.StartTimerRequest;
import com.exacta.timer.dto.timeentry.TimeEntryRequest;
import com.exacta.timer.dto.timeentry.TimeEntryResponse;
import com.exacta.timer.entity.TimeEntryStatus;
import com.exacta.timer.security.UserPrincipal;
import com.exacta.timer.service.TimeEntryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    public List<TimeEntryResponse> findAll(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TimeEntryStatus status,
            @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.findAll(projectId, status, actor);
    }

    @GetMapping("/active")
    public ResponseEntity<TimeEntryResponse> findActive(@AuthenticationPrincipal UserPrincipal actor) {
        TimeEntryResponse active = timeEntryService.findActive(actor);
        return active == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(active);
    }

    @GetMapping("/{id}")
    public TimeEntryResponse findById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.findById(id, actor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeEntryResponse create(
            @Valid @RequestBody TimeEntryRequest request, @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.create(request, actor);
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeEntryResponse start(
            @Valid @RequestBody StartTimerRequest request, @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.start(request, actor);
    }

    @PostMapping("/{id}/stop")
    public TimeEntryResponse stop(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.stop(id, actor);
    }

    @PutMapping("/{id}")
    public TimeEntryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody TimeEntryRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return timeEntryService.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal actor) {
        timeEntryService.delete(id, actor);
    }
}
