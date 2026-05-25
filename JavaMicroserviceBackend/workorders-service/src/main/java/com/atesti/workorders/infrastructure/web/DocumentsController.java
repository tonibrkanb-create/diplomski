package com.atesti.workorders.infrastructure.web;

import com.atesti.workorders.application.command.DocumentCommandService;
import com.atesti.workorders.application.dto.command.AddDocumentCommand;
import com.atesti.workorders.application.dto.query.DocumentResponse;
import com.atesti.workorders.domain.model.Document;
import com.atesti.workorders.domain.repository.DocumentRepository;
import com.atesti.workorders.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/radni-nalozi/{radniNalogId}/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentCommandService commandService;
    private final DocumentRepository documentRepository;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getByRadniNalog(@PathVariable Long radniNalogId) {
        List<DocumentResponse> docs = documentRepository.findByRadniNalogId(radniNalogId).stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docs);
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> add(@PathVariable Long radniNalogId, @RequestBody AddDocumentCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.add(radniNalogId, command));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long radniNalogId, @PathVariable Long documentId) {
        Document document = documentRepository.findByIdAndRadniNalogId(documentId, radniNalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", document.getId());
        payload.put("name", document.getName());
        payload.put("url", document.getUrl());
        payload.put("blob", document.getBlob() != null ? Base64.getEncoder().encodeToString(document.getBlob()) : null);
        payload.put("radniNalogId", document.getRadniNalogId());
        payload.put("createdAt", document.getCreatedAt());
        payload.put("updatedAt", document.getUpdatedAt());

        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long radniNalogId, @PathVariable Long documentId) {
        Document document = documentRepository.findByIdAndRadniNalogId(documentId, radniNalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", document.getName());

        return new ResponseEntity<>(document.getBlob(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> delete(@PathVariable Long radniNalogId, @PathVariable Long documentId) {
        return ResponseEntity.ok(commandService.delete(documentId));
    }
}
