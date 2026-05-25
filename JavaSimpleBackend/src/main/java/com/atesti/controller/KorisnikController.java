package com.atesti.controller;

import com.atesti.dto.*;
import com.atesti.entity.Obavijest;
import com.atesti.entity.Ponuda;
import com.atesti.entity.Recenzija;
import com.atesti.service.KorisnikAuthService;
import com.atesti.service.KorisnikObavijestiService;
import com.atesti.service.KorisnikPonudeService;
import com.atesti.service.KorisnikRecenzijeService;
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

    private final KorisnikAuthService korisnikAuthService;
    private final KorisnikPonudeService korisnikPonudeService;
    private final KorisnikObavijestiService korisnikObavijestiService;
    private final KorisnikRecenzijeService korisnikRecenzijeService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody KorisnikRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(korisnikAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody KorisnikLoginRequest request) {
        return ResponseEntity.ok(korisnikAuthService.login(request));
    }

    @GetMapping("/profil")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(korisnikAuthService.getProfile(getKorisnikId()));
    }

    @PutMapping("/profil")
    public ResponseEntity<?> updateProfile(@RequestBody KorisnikUpdateProfileRequest request) {
        return ResponseEntity.ok(korisnikAuthService.updateProfile(getKorisnikId(), request));
    }

    @GetMapping("/ponude")
    public ResponseEntity<List<Ponuda>> getPonude() {
        return ResponseEntity.ok(korisnikPonudeService.getByKorisnik(getKorisnikId()));
    }

    @PostMapping("/ponude")
    public ResponseEntity<Ponuda> createPonuda(@RequestBody PonudaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(korisnikPonudeService.create(getKorisnikId(), request));
    }

    @GetMapping("/ponude/{id}")
    public ResponseEntity<Ponuda> getPonuda(@PathVariable Long id) {
        return ResponseEntity.ok(korisnikPonudeService.getById(getKorisnikId(), id));
    }

    @GetMapping("/obavijesti")
    public ResponseEntity<List<Obavijest>> getObavijesti() {
        return ResponseEntity.ok(korisnikObavijestiService.getByKorisnik(getKorisnikId()));
    }

    @PutMapping("/obavijesti/{id}/procitaj")
    public ResponseEntity<Obavijest> markObavijestAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(korisnikObavijestiService.markAsRead(getKorisnikId(), id));
    }

    @GetMapping("/recenzije")
    public ResponseEntity<List<Recenzija>> getRecenzije() {
        return ResponseEntity.ok(korisnikRecenzijeService.getByKorisnik(getKorisnikId()));
    }

    @PostMapping("/recenzije")
    public ResponseEntity<Recenzija> createRecenzija(@RequestBody RecenzijaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(korisnikRecenzijeService.create(getKorisnikId(), request));
    }

    @GetMapping("/recenzije/{id}")
    public ResponseEntity<Recenzija> getRecenzija(@PathVariable Long id) {
        return ResponseEntity.ok(korisnikRecenzijeService.getById(getKorisnikId(), id));
    }

    private Long getKorisnikId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }
}
