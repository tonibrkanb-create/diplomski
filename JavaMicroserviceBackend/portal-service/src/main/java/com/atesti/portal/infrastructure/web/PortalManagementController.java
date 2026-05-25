package com.atesti.portal.infrastructure.web;

import com.atesti.portal.application.command.ObavijestCommandService;
import com.atesti.portal.application.command.PonudaCommandService;
import com.atesti.portal.application.command.RecenzijaCommandService;
import com.atesti.portal.application.dto.RespondRecenzijaCommand;
import com.atesti.portal.application.dto.SendObavijestCommand;
import com.atesti.portal.application.dto.UpdatePonudaStatusCommand;
import com.atesti.portal.application.dto.KorisnikProfileResponse;
import com.atesti.portal.application.dto.ObavijestResponse;
import com.atesti.portal.application.dto.PonudaResponse;
import com.atesti.portal.application.dto.RecenzijaResponse;
import com.atesti.portal.application.query.KorisnikQueryService;
import com.atesti.portal.application.query.PonudaQueryService;
import com.atesti.portal.application.query.RecenzijaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class PortalManagementController {

    private final PonudaCommandService ponudaCommandService;
    private final PonudaQueryService ponudaQueryService;
    private final RecenzijaCommandService recenzijaCommandService;
    private final RecenzijaQueryService recenzijaQueryService;
    private final ObavijestCommandService obavijestCommandService;
    private final KorisnikQueryService korisnikQueryService;

    @GetMapping("/ponude")
    public ResponseEntity<List<PonudaResponse>> getAllPonude() {
        return ResponseEntity.ok(ponudaQueryService.getAll());
    }

    @GetMapping("/ponude/{id}")
    public ResponseEntity<PonudaResponse> getPonuda(@PathVariable Long id) {
        return ResponseEntity.ok(ponudaQueryService.getByIdAdmin(id));
    }

    @PutMapping("/ponude/{id}/status")
    public ResponseEntity<PonudaResponse> updatePonudaStatus(@PathVariable Long id, @RequestBody UpdatePonudaStatusCommand command) {
        return ResponseEntity.ok(ponudaCommandService.updateStatus(id, command));
    }

    @GetMapping("/recenzije")
    public ResponseEntity<List<RecenzijaResponse>> getAllRecenzije() {
        return ResponseEntity.ok(recenzijaQueryService.getAll());
    }

    @PutMapping("/recenzije/{id}/odgovor")
    public ResponseEntity<RecenzijaResponse> respondRecenzija(@PathVariable Long id, @RequestBody RespondRecenzijaCommand command) {
        return ResponseEntity.ok(recenzijaCommandService.respond(id, command));
    }

    @PostMapping("/obavijesti")
    public ResponseEntity<ObavijestResponse> sendObavijest(@RequestBody SendObavijestCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obavijestCommandService.create(command));
    }

    @GetMapping("/korisnici")
    public ResponseEntity<List<KorisnikProfileResponse>> getKorisnici() {
        return ResponseEntity.ok(korisnikQueryService.getAllActive());
    }
}
