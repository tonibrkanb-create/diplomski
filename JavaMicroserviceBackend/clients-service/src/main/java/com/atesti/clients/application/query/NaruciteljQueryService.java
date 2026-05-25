package com.atesti.clients.application.query;

import com.atesti.clients.application.dto.NaruciteljPageResponse;
import com.atesti.clients.application.dto.NaruciteljResponse;
import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.clients.domain.repository.NaruciteljRepository;
import com.atesti.clients.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NaruciteljQueryService {

    private final NaruciteljRepository naruciteljRepository;

    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "name", "mjesto", "drzava", "postanskiBroj", "OIB", "email", "createdAt", "updatedAt"
    );

    public Object getAll(String search, Integer page, Integer pageSize,
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
            return naruciteljRepository.findAll(spec, sort).stream()
                    .map(NaruciteljResponse::from)
                    .toList();
        }

        int pageNum = Math.max(1, page != null ? page : 1);
        int pageSizeNum = pageSize != null ? Math.min(100, Math.max(1, pageSize)) : 10;

        Page<Narucitelj> result = naruciteljRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSizeNum, sort));

        return NaruciteljPageResponse.builder()
                .items(result.getContent().stream().map(NaruciteljResponse::from).toList())
                .page(pageNum)
                .pageSize(pageSizeNum)
                .totalItems(result.getTotalElements())
                .totalPages(Math.max(1, result.getTotalPages()))
                .sortBy(finalSortBy)
                .sortOrder(finalSortOrder)
                .build();
    }

    public NaruciteljResponse getById(Long id) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));
        return NaruciteljResponse.from(narucitelj);
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

            if (nameFilter != null && !nameFilter.isBlank())
                predicates.add(cb.like(root.get("name"), "%" + nameFilter.trim() + "%"));
            if (mjestoFilter != null && !mjestoFilter.isBlank())
                predicates.add(cb.like(root.get("mjesto"), "%" + mjestoFilter.trim() + "%"));
            if (drzavaFilter != null && !drzavaFilter.isBlank())
                predicates.add(cb.like(root.get("drzava"), "%" + drzavaFilter.trim() + "%"));
            if (postanskiBrojFilter != null && !postanskiBrojFilter.isBlank())
                predicates.add(cb.like(root.get("postanskiBroj"), "%" + postanskiBrojFilter.trim() + "%"));
            if (oibFilter != null && !oibFilter.isBlank())
                predicates.add(cb.like(root.get("OIB"), "%" + oibFilter.trim() + "%"));
            if (emailFilter != null && !emailFilter.isBlank())
                predicates.add(cb.like(root.get("email"), "%" + emailFilter.trim() + "%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
