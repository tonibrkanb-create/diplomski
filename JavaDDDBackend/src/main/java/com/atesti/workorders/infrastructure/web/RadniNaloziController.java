package com.atesti.workorders.infrastructure.web;

import com.atesti.workorders.application.command.RadniNalogDomainService;
import com.atesti.workorders.application.dto.command.AssignWorkerCommand;
import com.atesti.workorders.application.dto.command.CreateRadniNalogCommand;
import com.atesti.workorders.application.dto.command.UpdateRadniNalogCommand;
import com.atesti.workorders.application.dto.query.RadniNalogDetailResponse;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.application.dto.query.UskoroIsticeResponse;
import com.atesti.workorders.application.query.RadniNalogPdfService;
import com.atesti.workorders.application.query.RadniNalogQueryService;
import com.atesti.workorders.domain.model.RadniNalogProjection;
import com.atesti.workorders.infrastructure.web.model.AssignUserRequest;
import com.atesti.workorders.infrastructure.web.model.CreateRadniNalogRequest;
import com.atesti.workorders.infrastructure.web.model.UpdateRadniNalogRequest;
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

    private final RadniNalogDomainService commandService;
    private final RadniNalogQueryService queryService;
    private final RadniNalogPdfService pdfService;

    @GetMapping("/rn001.pdf")
    public ResponseEntity<byte[]> downloadRn001Pdf() {
        Map<String, Object> result = pdfService.generateRadniNalogPdf(Long.valueOf(1L));
        return buildPdfResponse(result, "RN001.pdf");
    }

    @GetMapping("/nextBrojNaloga")
    public ResponseEntity<Map<String, String>> getNextBrojNaloga() {
        String next = queryService.getNextBrojNaloga();
        return ResponseEntity.ok(Map.of("brojNaloga", next));
    }

    @GetMapping("/uskoroIstice")
    public ResponseEntity<List<UskoroIsticeResponse>> getUskoroIstice(
            @RequestParam(required = false) Integer days) {
        int daysValue = (days != null) ? days : 1000;
        return ResponseEntity.ok(queryService.getUskoroIstice(daysValue));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Map<String, Object> result = pdfService.generateRadniNalogPdf(id);
        String fileName = (String) result.get("fileName");
        return buildPdfResponse(result, fileName);
    }



    @GetMapping
    public ResponseEntity<List<RadniNalogResponse>> getAll() {
        return ResponseEntity.ok(queryService.getAllNalozi());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadniNalogDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }


    @PostMapping
    public ResponseEntity<RadniNalogResponse> create(@RequestBody CreateRadniNalogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(request.toCommand()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RadniNalogResponse> update(@PathVariable Long id, @RequestBody UpdateRadniNalogRequest request) {
        return ResponseEntity.ok(commandService.update(id, request.toCommand()));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<RadniNalogResponse> assignWorker(@PathVariable Long id, @RequestBody AssignUserRequest request) {
        return ResponseEntity.ok(commandService.assignWorker(id, request.toCommand()));
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
