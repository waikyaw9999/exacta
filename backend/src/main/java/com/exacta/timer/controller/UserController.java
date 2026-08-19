package com.exacta.timer.controller;

import com.exacta.timer.dto.user.UserRequest;
import com.exacta.timer.dto.user.UserResponse;
import com.exacta.timer.dto.user.UserUpdateRequest;
import com.exacta.timer.security.UserPrincipal;
import com.exacta.timer.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal actor) {
        return userService.findById(actor.id(), actor);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAll(@AuthenticationPrincipal UserPrincipal actor) {
        return userService.findAll(actor);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal actor) {
        return userService.findById(id, actor);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @Valid @RequestBody UserRequest request, @AuthenticationPrincipal UserPrincipal actor) {
        return userService.create(request, actor);
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return userService.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal actor) {
        userService.delete(id, actor);
    }
}
