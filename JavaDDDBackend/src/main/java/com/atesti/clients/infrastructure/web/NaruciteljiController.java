package com.atesti.clients.infrastructure.web;

import com.atesti.clients.application.command.NaruciteljCommandService;
import com.atesti.clients.application.dto.command.SaveNaruciteljCommand;
import com.atesti.clients.application.dto.query.NaruciteljResponse;
import com.atesti.clients.application.query.NaruciteljQueryService;
import com.atesti.workorders.application.query.RadniNalogQueryService;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/narucitelji")
@RequiredArgsConstructor
public class NaruciteljiController {

    private final NaruciteljCommandService commandService;
    private final NaruciteljQueryService queryService;
    private final RadniNalogQueryService radniNalogQueryService;

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

        Object result = queryService.getAll(searchTerm, page, pageSize,
                sortBy, sortOrder, name, mjesto, drzava, postanskiBroj, OIB, email);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NaruciteljResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<NaruciteljResponse> create(@RequestBody SaveNaruciteljCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NaruciteljResponse> update(@PathVariable Long id, @RequestBody SaveNaruciteljCommand command) {
        return ResponseEntity.ok(commandService.update(id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NaruciteljResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.delete(id));
    }

    @GetMapping("/{naruciteljiId}/radni-nalozi")
    public ResponseEntity<List<RadniNalogResponse>> getRadniNaloziByNarucitelj(@PathVariable Long naruciteljiId) {
        return ResponseEntity.ok(radniNalogQueryService.getByNaruciteljId(naruciteljiId));
    }
}
