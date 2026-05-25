package com.atesti.catalogue.infrastructure.web;

import com.atesti.catalogue.application.command.AktivnostCommandService;
import com.atesti.catalogue.application.dto.AktivnostResponse;
import com.atesti.catalogue.application.dto.SaveAktivnostCommand;
import com.atesti.catalogue.application.query.AktivnostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AktivnostiController {

    private final AktivnostCommandService commandService;
    private final AktivnostQueryService queryService;

    @GetMapping({"/api/aktivnosti", "/aktivnosti"})
    public ResponseEntity<List<AktivnostResponse>> getAll() {
        return ResponseEntity.ok(queryService.getAllActive());
    }

    @GetMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<AktivnostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    @PostMapping({"/api/aktivnosti", "/aktivnosti"})
    public ResponseEntity<AktivnostResponse> create(@RequestBody SaveAktivnostCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(command));
    }

    @PutMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<AktivnostResponse> update(@PathVariable Long id, @RequestBody SaveAktivnostCommand command) {
        return ResponseEntity.ok(commandService.update(id, command));
    }

    @DeleteMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<AktivnostResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.deactivate(id));
    }
}
