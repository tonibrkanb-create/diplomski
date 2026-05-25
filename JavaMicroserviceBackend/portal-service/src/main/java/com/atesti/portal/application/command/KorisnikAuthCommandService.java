package com.atesti.portal.application.command;

import com.atesti.portal.application.dto.KorisnikLoginCommand;
import com.atesti.portal.application.dto.KorisnikRegisterCommand;
import com.atesti.portal.application.dto.KorisnikUpdateProfileCommand;
import com.atesti.portal.application.dto.KorisnikProfileResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.exception.BadRequestException;
import com.atesti.portal.exception.ResourceNotFoundException;
import com.atesti.portal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KorisnikAuthCommandService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public Map<String, Object> register(KorisnikRegisterCommand command) {
        if (command.getEmail() == null || command.getPassword() == null) {
            throw new BadRequestException("Email i lozinka su obavezni");
        }
        if (korisnikRepository.existsByEmail(command.getEmail())) {
            throw new BadRequestException("Email vec postoji");
        }

        Korisnik korisnik = Korisnik.builder()
                .ime(command.getIme())
                .prezime(command.getPrezime())
                .email(command.getEmail())
                .telefon(command.getTelefon())
                .tvrtka(command.getTvrtka())
                .adresa(command.getAdresa())
                .mjesto(command.getMjesto())
                .postanskiBroj(command.getPostanskiBroj())
                .drzava(command.getDrzava())
                .password(passwordEncoder.encode(command.getPassword()))
                .isActive(true)
                .build();

        korisnik = korisnikRepository.save(korisnik);
        String token = tokenProvider.generateKorisnikToken(korisnik.getId(), korisnik.getEmail());

        return Map.of("korisnik", KorisnikProfileResponse.from(korisnik), "token", token);
    }

    public Map<String, Object> login(KorisnikLoginCommand command) {
        Korisnik korisnik = korisnikRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new BadRequestException("Neispravni podaci za prijavu"));

        if (!korisnik.getIsActive()) {
            throw new BadRequestException("Racun je deaktiviran");
        }

        if (!passwordEncoder.matches(command.getPassword(), korisnik.getPassword())) {
            throw new BadRequestException("Neispravni podaci za prijavu");
        }

        String token = tokenProvider.generateKorisnikToken(korisnik.getId(), korisnik.getEmail());
        return Map.of("korisnik", KorisnikProfileResponse.from(korisnik), "token", token);
    }

    @Transactional
    public KorisnikProfileResponse updateProfile(Long korisnikId, KorisnikUpdateProfileCommand command) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        korisnik.updateProfile(
                command.getIme(), command.getPrezime(), command.getTelefon(), command.getTvrtka(),
                command.getAdresa(), command.getMjesto(), command.getPostanskiBroj(), command.getDrzava()
        );

        korisnik = korisnikRepository.save(korisnik);
        return KorisnikProfileResponse.from(korisnik);
    }
}
