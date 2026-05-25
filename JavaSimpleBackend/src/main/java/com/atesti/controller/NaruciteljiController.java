package com.atesti.controller;

import com.atesti.dto.NaruciteljRequest;
import com.atesti.entity.Narucitelj;
import com.atesti.entity.RadniNalog;
import com.atesti.service.NaruciteljiService;
import com.atesti.service.RadniNaloziService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/narucitelji")
@RequiredArgsConstructor
public class NaruciteljiController {

    private final NaruciteljiService naruciteljiService;
    private final RadniNaloziService radniNaloziService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String narucitelj,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mjesto,
            @RequestParam(required = false) String drzava,
            @RequestParam(required = false) String postanskiBroj,
            @RequestParam(required = false) String OIB,
            @RequestParam(required = false) String email) {

        String searchTerm = q != null ? q : (search != null ? search : (narucitelj != null ? narucitelj : ""));

        Object result = naruciteljiService.getAllNarucitelji(searchTerm, page, pageSize,
                sortBy, sortOrder, name, mjesto, drzava, postanskiBroj, OIB, email);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Narucitelj> getById(@PathVariable Long id) {
        return ResponseEntity.ok(naruciteljiService.getNaruciteljiById(id));
    }

    @PostMapping
    public ResponseEntity<Narucitelj> create(@RequestBody NaruciteljRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(naruciteljiService.createNarucitelj(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Narucitelj> update(@PathVariable Long id, @RequestBody NaruciteljRequest request) {
        return ResponseEntity.ok(naruciteljiService.updateNarucitelj(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Narucitelj> delete(@PathVariable Long id) {
        return ResponseEntity.ok(naruciteljiService.deleteNarucitelj(id));
    }

    @GetMapping("/{naruciteljiId}/radni-nalozi")
    public ResponseEntity<List<RadniNalog>> getRadniNaloziByNarucitelj(@PathVariable Long naruciteljiId) {
        return ResponseEntity.ok(radniNaloziService.getRadniNaloziByNarucitelj(naruciteljiId));
    }
}
