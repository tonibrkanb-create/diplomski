package com.atesti.service;

import com.atesti.dto.DocumentRequest;
import com.atesti.entity.Document;
import com.atesti.entity.RadniNalog;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.DocumentRepository;
import com.atesti.repository.RadniNalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentsService {

    private final DocumentRepository documentRepository;
    private final RadniNalogRepository radniNalogRepository;

    public List<Document> getDocumentsByRadniNalog(Long radniNalogId) {
        return documentRepository.findByRadniNalogId(radniNalogId);
    }

    public Document addDocument(Long radniNalogId, DocumentRequest request) {
        RadniNalog nalog = radniNalogRepository.findById(radniNalogId)
                .orElseThrow(() -> new BadRequestException("Error adding document: Radni nalog not found"));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Error adding document: Document name is required");
        }

        if ((request.getBlob() == null || request.getBlob().isBlank()) && request.getUrl() == null) {
            throw new BadRequestException("Error adding document: Document blob is required (base64)");
        }

        Document.DocumentBuilder builder = Document.builder()
                .name(request.getName())
                .radniNalog(nalog);

        if (request.getBlob() != null && !request.getBlob().isBlank()) {
            byte[] blobBytes = parseBase64Blob(request.getBlob());
            builder.blob(blobBytes);
        } else {
            builder.url(request.getUrl());
        }

        return documentRepository.save(builder.build());
    }

    public Document getDocumentById(Long radniNalogId, Long documentId) {
        return documentRepository.findByIdAndRadniNalogId(documentId, radniNalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }

    public Document deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        documentRepository.delete(document);
        return document;
    }

    private byte[] parseBase64Blob(String blobInput) {
        if (blobInput == null || blobInput.isBlank()) {
            throw new BadRequestException("Error adding document: Document blob is required (base64)");
        }

        String cleaned = blobInput;
        if (cleaned.startsWith("data:")) {
            int commaIndex = cleaned.indexOf(',');
            if (commaIndex >= 0) {
                cleaned = cleaned.substring(commaIndex + 1);
            }
        }

        cleaned = cleaned.replaceAll("\\s", "");

        if (cleaned.isEmpty()) {
            throw new BadRequestException("Error adding document: Document blob is empty");
        }

        if (!cleaned.matches("^[A-Za-z0-9+/]*={0,2}$")) {
            throw new BadRequestException("Error adding document: Document blob must be valid base64");
        }

        return Base64.getDecoder().decode(cleaned);
    }
}
