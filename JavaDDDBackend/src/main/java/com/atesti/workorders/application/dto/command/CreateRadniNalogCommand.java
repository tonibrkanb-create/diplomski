package com.atesti.workorders.application.dto.command;

import java.util.List;

public record CreateRadniNalogCommand(
        String brojNaloga,
        Long naruciteljId,
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

    public CreateRadniNalogCommand {
        if (brojNaloga != null && !brojNaloga.matches("^RN\\d{3}$")) {
            throw new IllegalArgumentException("Broj naloga is invalid");
        }

        if (naruciteljId == null) {
            throw new IllegalArgumentException("Narucitelj id is required");
        }

        if (datum == null || datum.isBlank()) {
            throw new IllegalArgumentException("Datum is required");
        }

        if (objekt == null || objekt.isBlank()) {
            throw new IllegalArgumentException("Objekt is required");
        }

        if (fakturirano == null) {
            throw new IllegalArgumentException("Fakturirano is required");
        }

        if (zavrseno == null) {
            throw new IllegalArgumentException("Zavrseno is required");
        }

        if (opis == null || opis.isBlank()) {
            throw new IllegalArgumentException("Opis is required");
        }

        if (aktivnosti.isEmpty()) {
            throw new IllegalArgumentException("Aktivnosti cannot be empty");
        }
    }
}