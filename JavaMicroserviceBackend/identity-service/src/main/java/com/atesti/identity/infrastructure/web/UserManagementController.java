package com.atesti.identity.infrastructure.web;

import com.atesti.identity.application.command.UserCommandService;
import com.atesti.identity.application.dto.CreateUserCommand;
import com.atesti.identity.application.dto.UpdateUserCommand;
import com.atesti.identity.application.dto.UserResponse;
import com.atesti.identity.application.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserCommandService commandService;
    private final UserQueryService queryService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(queryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UpdateUserCommand command) {
        return ResponseEntity.ok(commandService.update(id, command));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.deactivate(id));
    }
}
