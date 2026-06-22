package com.atesti.workorders.application.dto.command;

import java.util.List;

public record UpdateRadniNalogCommand(
        String brojNaloga,
        Long naruciteljId,
        Long naruciteljIdAlt,
        String datum,
        String objekt,
        Boolean fakturirano,
        Boolean zavrseno,
        String opis,
        String brojPonude,
        String brojRacuna,
        String narudzbenica,
        String ugovor,
        List<Object> aktivnosti,
        String pdfUrl,
        Long assignedUserId
) {

    public UpdateRadniNalogCommand {
        if (naruciteljId == null && naruciteljIdAlt == null) {
            throw new IllegalArgumentException("Narucitelj id is required");
        }

        if (aktivnosti == null || aktivnosti.isEmpty()) {
            throw new IllegalArgumentException("Aktivnosti cannot be empty");
        }
    }
}