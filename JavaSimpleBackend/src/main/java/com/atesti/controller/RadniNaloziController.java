package com.atesti.controller;

import com.atesti.dto.AssignWorkerRequest;
import com.atesti.dto.RadniNalogRequest;
import com.atesti.dto.UskoroIsticeResponse;
import com.atesti.entity.RadniNalog;
import com.atesti.service.RadniNalogPdfService;
import com.atesti.service.RadniNaloziService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/radni-nalozi")
@RequiredArgsConstructor
public class RadniNaloziController {

    private final RadniNaloziService radniNaloziService;
    private final RadniNalogPdfService radniNalogPdfService;

    @GetMapping("/rn001.pdf")
    public ResponseEntity<byte[]> downloadRn001Pdf() {
        Map<String, Object> result = radniNalogPdfService.generateRadniNalogPdf(1L);
        return buildPdfResponse(result, "RN001.pdf");
    }

    @GetMapping("/nextBrojNaloga")
    public ResponseEntity<Map<String, String>> getNextBrojNaloga() {
        String next = radniNaloziService.getNextBrojNaloga();
        return ResponseEntity.ok(Map.of("brojNaloga", next));
    }

    @GetMapping("/uskoroIstice")
    public ResponseEntity<List<UskoroIsticeResponse>> getUskoroIstice(
            @RequestParam(required = false) Integer days) {
        int daysValue = (days != null) ? days : 1000;
        return ResponseEntity.ok(radniNaloziService.getUskoroIstice(daysValue));
    }

    @GetMapping
    public ResponseEntity<List<RadniNalog>> getAll() {
        return ResponseEntity.ok(radniNaloziService.getAllRadniNalozi());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadniNalog> getById(@PathVariable Long id) {
        return ResponseEntity.ok(radniNaloziService.getRadniNalogById(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Map<String, Object> result = radniNalogPdfService.generateRadniNalogPdf(id);
        String fileName = (String) result.get("fileName");
        return buildPdfResponse(result, fileName);
    }

    @PostMapping
    public ResponseEntity<RadniNalog> create(@RequestBody RadniNalogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(radniNaloziService.createRadniNalog(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadniNalog> update(@PathVariable Long id, @RequestBody RadniNalogRequest request) {
        return ResponseEntity.ok(radniNaloziService.updateRadniNalog(id, request));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<RadniNalog> assignWorker(@PathVariable Long id, @RequestBody AssignWorkerRequest request) {
        return ResponseEntity.ok(radniNaloziService.assignWorker(id, request.getAssignedUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RadniNalog> delete(@PathVariable Long id) {
        return ResponseEntity.ok(radniNaloziService.deleteRadniNalog(id));
    }

    private ResponseEntity<byte[]> buildPdfResponse(Map<String, Object> result, String fileName) {
        byte[] buffer = (byte[]) result.get("buffer");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
    }
}
