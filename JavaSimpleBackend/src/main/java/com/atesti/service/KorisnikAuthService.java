package com.atesti.service;

import com.atesti.dto.KorisnikLoginRequest;
import com.atesti.dto.KorisnikRegisterRequest;
import com.atesti.dto.KorisnikUpdateProfileRequest;
import com.atesti.entity.Korisnik;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.KorisnikRepository;
import com.atesti.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KorisnikAuthService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public Map<String, Object> register(KorisnikRegisterRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email i lozinka su obavezni");
        }
        if (korisnikRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email već postoji");
        }

        Korisnik korisnik = Korisnik.builder()
                .ime(request.getIme())
                .prezime(request.getPrezime())
                .email(request.getEmail())
                .telefon(request.getTelefon())
                .tvrtka(request.getTvrtka())
                .adresa(request.getAdresa())
                .mjesto(request.getMjesto())
                .postanskiBroj(request.getPostanskiBroj())
                .drzava(request.getDrzava())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        korisnik = korisnikRepository.save(korisnik);
        String token = tokenProvider.generateKorisnikToken(korisnik.getId(), korisnik.getEmail());

        return Map.of("korisnik", korisnik, "token", token);
    }

    public Map<String, Object> login(KorisnikLoginRequest request) {
        Korisnik korisnik = korisnikRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Neispravni podaci za prijavu"));

        if (!korisnik.getIsActive()) {
            throw new BadRequestException("Račun je deaktiviran");
        }

        if (!passwordEncoder.matches(request.getPassword(), korisnik.getPassword())) {
            throw new BadRequestException("Neispravni podaci za prijavu");
        }

        String token = tokenProvider.generateKorisnikToken(korisnik.getId(), korisnik.getEmail());
        return Map.of("korisnik", korisnik, "token", token);
    }

    public Korisnik getProfile(Long korisnikId) {
        return korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));
    }

    @Transactional
    public Korisnik updateProfile(Long korisnikId, KorisnikUpdateProfileRequest request) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        if (request.getIme() != null) korisnik.setIme(request.getIme());
        if (request.getPrezime() != null) korisnik.setPrezime(request.getPrezime());
        if (request.getTelefon() != null) korisnik.setTelefon(request.getTelefon());
        if (request.getTvrtka() != null) korisnik.setTvrtka(request.getTvrtka());
        if (request.getAdresa() != null) korisnik.setAdresa(request.getAdresa());
        if (request.getMjesto() != null) korisnik.setMjesto(request.getMjesto());
        if (request.getPostanskiBroj() != null) korisnik.setPostanskiBroj(request.getPostanskiBroj());
        if (request.getDrzava() != null) korisnik.setDrzava(request.getDrzava());

        return korisnikRepository.save(korisnik);
    }
}
