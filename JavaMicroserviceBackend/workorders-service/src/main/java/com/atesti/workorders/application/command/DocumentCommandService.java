package com.atesti.workorders.application.command;

import com.atesti.workorders.application.dto.command.AddDocumentCommand;
import com.atesti.workorders.application.dto.query.DocumentResponse;
import com.atesti.workorders.domain.model.Document;
import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.domain.repository.DocumentRepository;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.atesti.workorders.exception.BadRequestException;
import com.atesti.workorders.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DocumentCommandService {

    private final DocumentRepository documentRepository;
    private final RadniNalogRepository radniNalogRepository;

    @Transactional
    public DocumentResponse add(Long radniNalogId, AddDocumentCommand command) {
        RadniNalog nalog = radniNalogRepository.findById(radniNalogId)
                .orElseThrow(() -> new BadRequestException("Error adding document: Radni nalog not found"));

        if (command.getName() == null || command.getName().isBlank()) {
            throw new BadRequestException("Error adding document: Document name is required");
        }

        if ((command.getBlob() == null || command.getBlob().isBlank()) && command.getUrl() == null) {
            throw new BadRequestException("Error adding document: Document blob is required (base64)");
        }

        Document.DocumentBuilder builder = Document.builder()
                .name(command.getName())
                .radniNalog(nalog);

        if (command.getBlob() != null && !command.getBlob().isBlank()) {
            byte[] blobBytes = parseBase64Blob(command.getBlob());
            builder.blob(blobBytes);
        } else {
            builder.url(command.getUrl());
        }

        Document saved = documentRepository.save(builder.build());
        return DocumentResponse.from(saved);
    }

    @Transactional
    public DocumentResponse delete(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        DocumentResponse response = DocumentResponse.from(document);
        documentRepository.delete(document);
        return response;
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
