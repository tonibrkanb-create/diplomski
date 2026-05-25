package com.atesti.identity.application.command;

import com.atesti.identity.application.dto.AuthResponse;
import com.atesti.identity.application.dto.LoginCommand;
import com.atesti.identity.application.dto.RegisterCommand;
import com.atesti.identity.domain.model.User;
import com.atesti.identity.domain.repository.UserRepository;
import com.atesti.identity.exception.BadRequestException;
import com.atesti.identity.infrastructure.kafka.UserEventPublisher;
import com.atesti.identity.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserEventPublisher userEventPublisher;

    @Transactional
    public AuthResponse register(RegisterCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new BadRequestException("Error creating user: Username already exists");
        }

        User user = User.builder()
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .build();

        user = userRepository.save(user);
        userEventPublisher.publish("CREATED", user);

        Map<String, Object> safeUser = new LinkedHashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());

        String token = tokenProvider.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(safeUser, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginCommand command) {
        User user = userRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Login failed: Invalid credentials"));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Login failed: Invalid credentials");
        }

        Map<String, Object> safeUser = new LinkedHashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("createdAt", user.getCreatedAt());
        safeUser.put("updatedAt", user.getUpdatedAt());

        String token = tokenProvider.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(safeUser, token);
    }
}
