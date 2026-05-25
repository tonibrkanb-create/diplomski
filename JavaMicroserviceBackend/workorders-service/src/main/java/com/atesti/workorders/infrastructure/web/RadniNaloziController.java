package com.atesti.workorders.infrastructure.web;

import com.atesti.workorders.application.command.RadniNalogCommandService;
import com.atesti.workorders.application.dto.command.AssignWorkerCommand;
import com.atesti.workorders.application.dto.command.CreateRadniNalogCommand;
import com.atesti.workorders.application.dto.command.UpdateRadniNalogCommand;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.application.dto.query.UskoroIsticeResponse;
import com.atesti.workorders.application.query.RadniNalogPdfService;
import com.atesti.workorders.application.query.RadniNalogQueryService;
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

    private final RadniNalogCommandService commandService;
    private final RadniNalogQueryService queryService;
    private final RadniNalogPdfService pdfService;

    @GetMapping("/rn001.pdf")
    public ResponseEntity<byte[]> downloadRn001Pdf() {
        Map<String, Object> result = pdfService.generateRadniNalogPdf(1L);
        return buildPdfResponse(result, "RN001.pdf");
    }

    @GetMapping("/nextBrojNaloga")
    public ResponseEntity<Map<String, String>> getNextBrojNaloga() {
        String next = commandService.getNextBrojNaloga();
        return ResponseEntity.ok(Map.of("brojNaloga", next));
    }

    @GetMapping("/uskoroIstice")
    public ResponseEntity<List<UskoroIsticeResponse>> getUskoroIstice(
            @RequestParam(required = false) Integer days) {
        int daysValue = (days != null) ? days : 1000;
        return ResponseEntity.ok(queryService.getUskoroIstice(daysValue));
    }

    @GetMapping
    public ResponseEntity<List<RadniNalogResponse>> getAll() {
        return ResponseEntity.ok(queryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadniNalogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Map<String, Object> result = pdfService.generateRadniNalogPdf(id);
        String fileName = (String) result.get("fileName");
        return buildPdfResponse(result, fileName);
    }

    @PostMapping
    public ResponseEntity<RadniNalogResponse> create(@RequestBody CreateRadniNalogCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadniNalogResponse> update(@PathVariable Long id, @RequestBody UpdateRadniNalogCommand command) {
        return ResponseEntity.ok(commandService.update(id, command));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<RadniNalogResponse> assignWorker(@PathVariable Long id, @RequestBody AssignWorkerCommand command) {
        return ResponseEntity.ok(commandService.assignWorker(id, command.getAssignedUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RadniNalogResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.delete(id));
    }

    private ResponseEntity<byte[]> buildPdfResponse(Map<String, Object> result, String fileName) {
        byte[] buffer = (byte[]) result.get("buffer");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
    }
}
