package com.atesti.controller;

import com.atesti.dto.DocumentRequest;
import com.atesti.entity.Document;
import com.atesti.service.DocumentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/radni-nalozi/{radniNalogId}/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentsService documentsService;

    @GetMapping
    public ResponseEntity<List<Document>> getByRadniNalog(@PathVariable Long radniNalogId) {
        return ResponseEntity.ok(documentsService.getDocumentsByRadniNalog(radniNalogId));
    }

    @PostMapping
    public ResponseEntity<Document> add(@PathVariable Long radniNalogId, @RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentsService.addDocument(radniNalogId, request));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long radniNalogId, @PathVariable Long documentId) {
        Document document = documentsService.getDocumentById(radniNalogId, documentId);

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
        Document document = documentsService.getDocumentById(radniNalogId, documentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", document.getName());

        return new ResponseEntity<>(document.getBlob(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Document> delete(@PathVariable Long radniNalogId, @PathVariable Long documentId) {
        return ResponseEntity.ok(documentsService.deleteDocument(documentId));
    }
}
