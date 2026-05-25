package com.atesti.controller;

import com.atesti.dto.AktivnostRequest;
import com.atesti.entity.Aktivnost;
import com.atesti.service.AktivnostiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AktivnostiController {

    private final AktivnostiService aktivnostiService;

    @GetMapping({"/api/aktivnosti", "/aktivnosti"})
    public ResponseEntity<List<Aktivnost>> getAll() {
        return ResponseEntity.ok(aktivnostiService.getAllAktivnosti());
    }

    @GetMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<Aktivnost> getById(@PathVariable Long id) {
        return ResponseEntity.ok(aktivnostiService.getAktivnostById(id));
    }

    @PostMapping({"/api/aktivnosti", "/aktivnosti"})
    public ResponseEntity<Aktivnost> create(@RequestBody AktivnostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aktivnostiService.createAktivnost(request));
    }

    @PutMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<Aktivnost> update(@PathVariable Long id, @RequestBody AktivnostRequest request) {
        return ResponseEntity.ok(aktivnostiService.updateAktivnost(id, request));
    }

    @DeleteMapping({"/api/aktivnosti/{id}", "/aktivnosti/{id}"})
    public ResponseEntity<Aktivnost> delete(@PathVariable Long id) {
        return ResponseEntity.ok(aktivnostiService.deleteAktivnost(id));
    }
}
