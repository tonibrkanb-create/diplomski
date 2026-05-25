package com.atesti.service;

import com.atesti.dto.AuthResponse;
import com.atesti.dto.LoginRequest;
import com.atesti.dto.RegisterRequest;
import com.atesti.entity.User;
import com.atesti.exception.BadRequestException;
import com.atesti.repository.UserRepository;
import com.atesti.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Error creating user: Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        Map<String, Object> safeUser = new LinkedHashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());

        String token = tokenProvider.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(safeUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Login failed: Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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
