package com.atesti.identity.infrastructure.web;

import com.atesti.identity.application.command.AuthCommandService;
import com.atesti.identity.application.dto.AuthResponse;
import com.atesti.identity.application.dto.LoginCommand;
import com.atesti.identity.application.dto.RegisterCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authCommandService.register(command));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginCommand command) {
        return ResponseEntity.ok(authCommandService.login(command));
    }
}
