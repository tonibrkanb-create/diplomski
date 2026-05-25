package com.atesti.portal.infrastructure.web;

import com.atesti.portal.application.command.KorisnikAuthCommandService;
import com.atesti.portal.application.command.ObavijestCommandService;
import com.atesti.portal.application.command.PonudaCommandService;
import com.atesti.portal.application.command.RecenzijaCommandService;
import com.atesti.portal.application.dto.command.CreatePonudaCommand;
import com.atesti.portal.application.dto.command.CreateRecenzijaCommand;
import com.atesti.portal.application.dto.command.KorisnikLoginCommand;
import com.atesti.portal.application.dto.command.KorisnikRegisterCommand;
import com.atesti.portal.application.dto.command.KorisnikUpdateProfileCommand;
import com.atesti.portal.application.dto.query.KorisnikProfileResponse;
import com.atesti.portal.application.dto.query.ObavijestResponse;
import com.atesti.portal.application.dto.query.PonudaResponse;
import com.atesti.portal.application.dto.query.RecenzijaResponse;
import com.atesti.portal.application.query.KorisnikQueryService;
import com.atesti.portal.application.query.ObavijestQueryService;
import com.atesti.portal.application.query.PonudaQueryService;
import com.atesti.portal.application.query.RecenzijaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/korisnik")
@RequiredArgsConstructor
public class KorisnikController {

    private final KorisnikAuthCommandService authCommandService;
    private final KorisnikQueryService korisnikQueryService;
    private final PonudaCommandService ponudaCommandService;
    private final PonudaQueryService ponudaQueryService;
    private final ObavijestCommandService obavijestCommandService;
    private final ObavijestQueryService obavijestQueryService;
    private final RecenzijaCommandService recenzijaCommandService;
    private final RecenzijaQueryService recenzijaQueryService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody KorisnikRegisterCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authCommandService.register(command));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody KorisnikLoginCommand command) {
        return ResponseEntity.ok(authCommandService.login(command));
    }

    @GetMapping("/profil")
    public ResponseEntity<KorisnikProfileResponse> getProfile() {
        return ResponseEntity.ok(korisnikQueryService.getProfile(getKorisnikId()));
    }

    @PutMapping("/profil")
    public ResponseEntity<KorisnikProfileResponse> updateProfile(@RequestBody KorisnikUpdateProfileCommand command) {
        return ResponseEntity.ok(authCommandService.updateProfile(getKorisnikId(), command));
    }

    @GetMapping("/ponude")
    public ResponseEntity<List<PonudaResponse>> getPonude() {
        return ResponseEntity.ok(ponudaQueryService.getByKorisnik(getKorisnikId()));
    }

    @PostMapping("/ponude")
    public ResponseEntity<PonudaResponse> createPonuda(@RequestBody CreatePonudaCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ponudaCommandService.create(getKorisnikId(), command));
    }

    @GetMapping("/ponude/{id}")
    public ResponseEntity<PonudaResponse> getPonuda(@PathVariable Long id) {
        return ResponseEntity.ok(ponudaQueryService.getById(id, getKorisnikId()));
    }

    @GetMapping("/obavijesti")
    public ResponseEntity<List<ObavijestResponse>> getObavijesti() {
        return ResponseEntity.ok(obavijestQueryService.getByKorisnik(getKorisnikId()));
    }

    @PutMapping("/obavijesti/{id}/procitaj")
    public ResponseEntity<ObavijestResponse> markObavijestAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(obavijestCommandService.markAsRead(id, getKorisnikId()));
    }

    @GetMapping("/recenzije")
    public ResponseEntity<List<RecenzijaResponse>> getRecenzije() {
        return ResponseEntity.ok(recenzijaQueryService.getByKorisnik(getKorisnikId()));
    }

    @PostMapping("/recenzije")
    public ResponseEntity<RecenzijaResponse> createRecenzija(@RequestBody CreateRecenzijaCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recenzijaCommandService.create(getKorisnikId(), command));
    }

    @GetMapping("/recenzije/{id}")
    public ResponseEntity<RecenzijaResponse> getRecenzija(@PathVariable Long id) {
        return ResponseEntity.ok(recenzijaQueryService.getById(id, getKorisnikId()));
    }

    private Long getKorisnikId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }
}
