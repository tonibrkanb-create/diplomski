package com.atesti.service;

import com.atesti.dto.NaruciteljRequest;
import com.atesti.dto.NaruciteljiPageResponse;
import com.atesti.entity.Narucitelj;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.NaruciteljRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaruciteljiService {

    private final NaruciteljRepository naruciteljRepository;

    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "name", "mjesto", "drzava", "postanskiBroj", "OIB", "email", "createdAt", "updatedAt"
    );

    public Object getAllNarucitelji(String search, Integer page, Integer pageSize,
                                   String sortBy, String sortOrder,
                                   String nameFilter, String mjestoFilter,
                                   String drzavaFilter, String postanskiBrojFilter,
                                   String oibFilter, String emailFilter) {

        boolean hasGridParams = page != null || pageSize != null || sortBy != null || sortOrder != null
                || nameFilter != null || mjestoFilter != null || drzavaFilter != null
                || postanskiBrojFilter != null || oibFilter != null || emailFilter != null;

        String finalSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "name";
        String finalSortOrder = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        Sort sort = Sort.by(Sort.Direction.fromString(finalSortOrder), finalSortBy);

        Specification<Narucitelj> spec = buildSpecification(search, nameFilter, mjestoFilter,
                drzavaFilter, postanskiBrojFilter, oibFilter, emailFilter);

        if (!hasGridParams) {
            List<Narucitelj> rows = naruciteljRepository.findAll(spec, sort);
            return rows;
        }

        int pageNum = Math.max(1, page != null ? page : 1);
        int pageSizeNum = pageSize != null ? Math.min(100, Math.max(1, pageSize)) : 10;

        Page<Narucitelj> result = naruciteljRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSizeNum, sort));

        return NaruciteljiPageResponse.builder()
                .items(result.getContent())
                .page(pageNum)
                .pageSize(pageSizeNum)
                .totalItems(result.getTotalElements())
                .totalPages(Math.max(1, result.getTotalPages()))
                .sortBy(finalSortBy)
                .sortOrder(finalSortOrder)
                .build();
    }

    public Narucitelj getNaruciteljiById(Long id) {
        return naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));
    }

    public Narucitelj createNarucitelj(NaruciteljRequest request) {
        String nameValue = request.getNarucitelj() != null ? request.getNarucitelj() : request.getName();
        if (nameValue == null || nameValue.isBlank()) {
            throw new BadRequestException("Error creating narucitelj: name is required");
        }

        Narucitelj narucitelj = Narucitelj.builder()
                .name(nameValue)
                .adresa(request.getAdresa())
                .mjesto(request.getMjesto())
                .postanskiBroj(request.getPostanskiBroj())
                .drzava(request.getDrzava())
                .OIB(request.getOIB())
                .ziroRacun(request.getZiroRacun())
                .ostalo(request.getOstalo())
                .kontaktOsoba(request.getKontaktOsoba())
                .telefon(request.getTelefon())
                .mobitel(request.getMobitel())
                .fax(request.getFax())
                .email(request.getEmail())
                .location(request.getMjesto() != null ? request.getMjesto() : "")
                .comment(request.getComment())
                .build();

        return naruciteljRepository.save(narucitelj);
    }

    public Narucitelj updateNarucitelj(Long id, NaruciteljRequest request) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));

        String nameValue = request.getNarucitelj() != null ? request.getNarucitelj() : request.getName();
        if (nameValue != null) {
            narucitelj.setName(nameValue);
        }
        if (request.getAdresa() != null) narucitelj.setAdresa(request.getAdresa());
        if (request.getMjesto() != null) narucitelj.setMjesto(request.getMjesto());
        if (request.getPostanskiBroj() != null) narucitelj.setPostanskiBroj(request.getPostanskiBroj());
        if (request.getDrzava() != null) narucitelj.setDrzava(request.getDrzava());
        if (request.getOIB() != null) narucitelj.setOIB(request.getOIB());
        if (request.getZiroRacun() != null) narucitelj.setZiroRacun(request.getZiroRacun());
        if (request.getOstalo() != null) narucitelj.setOstalo(request.getOstalo());
        if (request.getKontaktOsoba() != null) narucitelj.setKontaktOsoba(request.getKontaktOsoba());
        if (request.getTelefon() != null) narucitelj.setTelefon(request.getTelefon());
        if (request.getMobitel() != null) narucitelj.setMobitel(request.getMobitel());
        if (request.getFax() != null) narucitelj.setFax(request.getFax());
        if (request.getEmail() != null) narucitelj.setEmail(request.getEmail());
        if (request.getLocation() != null) narucitelj.setLocation(request.getLocation());
        if (request.getComment() != null) narucitelj.setComment(request.getComment());

        return naruciteljRepository.save(narucitelj);
    }

    public Narucitelj deleteNarucitelj(Long id) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));

        naruciteljRepository.delete(narucitelj);
        return narucitelj;
    }

    private Specification<Narucitelj> buildSpecification(String search, String nameFilter,
                                                         String mjestoFilter, String drzavaFilter,
                                                         String postanskiBrojFilter, String oibFilter,
                                                         String emailFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(root.get("name"), pattern),
                        cb.like(root.get("mjesto"), pattern),
                        cb.like(root.get("adresa"), pattern),
                        cb.like(root.get("email"), pattern),
                        cb.like(root.get("OIB"), pattern)
                );
                predicates.add(searchPredicate);
            }

            if (nameFilter != null && !nameFilter.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + nameFilter.trim() + "%"));
            }
            if (mjestoFilter != null && !mjestoFilter.isBlank()) {
                predicates.add(cb.like(root.get("mjesto"), "%" + mjestoFilter.trim() + "%"));
            }
            if (drzavaFilter != null && !drzavaFilter.isBlank()) {
                predicates.add(cb.like(root.get("drzava"), "%" + drzavaFilter.trim() + "%"));
            }
            if (postanskiBrojFilter != null && !postanskiBrojFilter.isBlank()) {
                predicates.add(cb.like(root.get("postanskiBroj"), "%" + postanskiBrojFilter.trim() + "%"));
            }
            if (oibFilter != null && !oibFilter.isBlank()) {
                predicates.add(cb.like(root.get("OIB"), "%" + oibFilter.trim() + "%"));
            }
            if (emailFilter != null && !emailFilter.isBlank()) {
                predicates.add(cb.like(root.get("email"), "%" + emailFilter.trim() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
