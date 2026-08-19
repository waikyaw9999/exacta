package com.exacta.timer.service;

import com.exacta.timer.dto.auth.AuthResponse;
import com.exacta.timer.dto.auth.LoginRequest;
import com.exacta.timer.dto.auth.RegisterRequest;
import com.exacta.timer.entity.Role;
import com.exacta.timer.entity.User;
import com.exacta.timer.exception.ConflictException;
import com.exacta.timer.repository.UserRepository;
import com.exacta.timer.security.JwtService;
import com.exacta.timer.security.UserPrincipal;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email is already in use");
        }

        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.MEMBER;
        User user = User.builder()
                .name(request.name().trim())
                .email(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .hourlyRate(request.hourlyRate() != null ? request.hourlyRate() : BigDecimal.ZERO)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered user {} with role {}", saved.getEmail(), saved.getRole());
        return issueToken(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ConflictException("Invalid email or password"));
        return issueToken(user);
    }

    private AuthResponse issueToken(User user) {
        String token = jwtService.generateToken(UserPrincipal.from(user));
        return AuthResponse.bearer(token, userService.toResponse(user));
    }
}
