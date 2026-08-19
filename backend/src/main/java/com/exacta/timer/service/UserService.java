package com.exacta.timer.service;

import com.exacta.timer.dto.user.UserRequest;
import com.exacta.timer.dto.user.UserResponse;
import com.exacta.timer.dto.user.UserUpdateRequest;
import com.exacta.timer.entity.User;
import com.exacta.timer.exception.ConflictException;
import com.exacta.timer.exception.ResourceNotFoundException;
import com.exacta.timer.repository.TimeEntryRepository;
import com.exacta.timer.repository.UserRepository;
import com.exacta.timer.security.UserPrincipal;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll(UserPrincipal actor) {
        requireAdmin(actor);
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse findById(Long id, UserPrincipal actor) {
        requireSelfOrAdmin(id, actor);
        return toResponse(getById(id));
    }

    @Transactional
    public UserResponse create(UserRequest request, UserPrincipal actor) {
        requireAdmin(actor);
        ensureEmailAvailable(request.email(), null);
        User user = User.builder()
                .name(request.name().trim())
                .email(normalizeEmail(request.email()))
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .hourlyRate(request.hourlyRate())
                .build();
        User saved = userRepository.save(user);
        log.info("Admin {} created user {}", actor.email(), saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request, UserPrincipal actor) {
        requireSelfOrAdmin(id, actor);
        User user = getById(id);

        if (request.name() != null) {
            user.setName(request.name().trim());
        }
        if (request.email() != null) {
            ensureEmailAvailable(request.email(), id);
            user.setEmail(normalizeEmail(request.email()));
        }
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.hourlyRate() != null) {
            user.setHourlyRate(request.hourlyRate());
        }
        if (request.role() != null && request.role() != user.getRole()) {
            requireAdmin(actor);
            user.setRole(request.role());
        }

        return toResponse(user);
    }

    @Transactional
    public void delete(Long id, UserPrincipal actor) {
        requireAdmin(actor);
        if (actor.id().equals(id)) {
            throw new ConflictException("You cannot delete your own account");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        if (timeEntryRepository.existsByUserId(id)) {
            throw new ConflictException("Cannot delete a user who has time entries");
        }
        userRepository.deleteById(id);
        log.info("Admin {} deleted user {}", actor.email(), id);
    }

    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getHourlyRate() != null ? user.getHourlyRate() : BigDecimal.ZERO);
    }

    private void ensureEmailAvailable(String email, Long currentId) {
        boolean taken = currentId == null
                ? userRepository.existsByEmailIgnoreCase(email)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (taken) {
            throw new ConflictException("Email is already in use");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void requireAdmin(UserPrincipal actor) {
        if (!actor.isAdmin()) {
            throw new AccessDeniedException("Admin role required");
        }
    }

    private void requireSelfOrAdmin(Long id, UserPrincipal actor) {
        if (!actor.isAdmin() && !actor.id().equals(id)) {
            throw new AccessDeniedException("You can only access your own user profile");
        }
    }
}
