package com.atesti.service;

import com.atesti.entity.Aktivnost;
import com.atesti.entity.Narucitelj;
import com.atesti.entity.RadniNalog;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.AktivnostRepository;
import com.atesti.repository.RadniNalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadniNalogPdfService {

    private final RadniNalogRepository radniNalogRepository;
    private final AktivnostRepository aktivnostRepository;
    private final ObjectMapper objectMapper;

    public Map<String, Object> generateRadniNalogPdf(Long radniNalogId) {
        RadniNalog nalog = radniNalogRepository.findById(radniNalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));

        List<String> aktivnosti = resolveAktivnosti(nalog.getAktivnosti());
        Narucitelj narucitelj = nalog.getNarucitelj();

        byte[] pdfBuffer = buildPdfBuffer(nalog, narucitelj, aktivnosti);

        String fileName = buildFileName(nalog);

        Map<String, Object> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("contentType", "application/pdf");
        result.put("buffer", pdfBuffer);
        return result;
    }

    private byte[] buildPdfBuffer(RadniNalog nalog, Narucitelj narucitelj, List<String> aktivnosti) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 52, 52, 48, 48);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 28, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
        Font headerFont = new Font(Font.HELVETICA, 19, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 11, Font.ITALIC);
        Font valueFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
        Font valueBoldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font valueLargeFont = new Font(Font.HELVETICA, 15, Font.BOLD);
        Font valueNameFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        // Header
        Paragraph title = new Paragraph("ATEST TEAM", titleFont);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                "ZAŠTITA NA RADU, ZAŠTITA OD POŽARA, TEHNIČKO SAVJETOVANJE I MINIMALNI TEHNIČKI UVJETI",
                subtitleFont);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);

        // Separator line
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        PdfPCell sepCell = new PdfPCell();
        sepCell.setBorderWidthTop(1.1f);
        sepCell.setBorderWidthBottom(0);
        sepCell.setBorderWidthLeft(0);
        sepCell.setBorderWidthRight(0);
        sepCell.setFixedHeight(2);
        separator.addCell(sepCell);
        document.add(separator);

        // Title box - RADNI NALOG
        PdfPTable titleBox = new PdfPTable(1);
        titleBox.setWidthPercentage(100);
        PdfPCell titleCell = new PdfPCell(new Phrase("RADNI NALOG", headerFont));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setFixedHeight(34);
        titleCell.setBackgroundColor(new Color(232, 232, 232));
        titleBox.addCell(titleCell);
        titleBox.setSpacingAfter(15);
        document.add(titleBox);

        // Broj radnog naloga
        addLabeledRow(document, "Broj radnog naloga", nalog.getBrojNaloga(), labelFont, valueLargeFont);

        // Narucitelj info
        addLabeledRow(document, "Naručitelj", narucitelj != null ? narucitelj.getName() : "", labelFont, valueNameFont);
        addLabeledRow(document, "Adresa naručitelja", joinAddress(narucitelj), labelFont, valueFont);
        addLabeledRow(document, "Kontakt", narucitelj != null ? narucitelj.getKontaktOsoba() : "", labelFont, valueBoldFont);

        // Contact details
        addLabeledRow(document, "Telefon", narucitelj != null ? narucitelj.getTelefon() : "", labelFont, valueFont);
        addLabeledRow(document, "Mobitel", narucitelj != null ? narucitelj.getMobitel() : "", labelFont, valueFont);
        addLabeledRow(document, "Fax", narucitelj != null ? narucitelj.getFax() : "", labelFont, valueFont);
        addLabeledRow(document, "E-mail", narucitelj != null ? narucitelj.getEmail() : "", labelFont, valueFont);
        addLabeledRow(document, "OIB", narucitelj != null ? narucitelj.getOIB() : "", labelFont, valueFont);

        // Date and object
        addLabeledRow(document, "Datum", formatDate(nalog.getDatum()), labelFont, valueBoldFont);
        addLabeledRow(document, "Objekt - Lokacija", nalog.getObjekt(), labelFont, valueBoldFont);

        // Aktivnosti
        Paragraph sectionHeader = new Paragraph("Radne aktivnosti", sectionFont);
        sectionHeader.setSpacingBefore(10);
        sectionHeader.setSpacingAfter(5);
        document.add(sectionHeader);

        if (!aktivnosti.isEmpty()) {
            for (int i = 0; i < aktivnosti.size(); i++) {
                Paragraph item = new Paragraph(String.format("    %d. %s", i + 1, aktivnosti.get(i)), valueFont);
                document.add(item);
            }
        }

        // Additional fields
        addLabeledRow(document, "Komentari aktivnosti", nalog.getOpis(), labelFont, valueFont);
        addLabeledRow(document, "Ostale aktivnosti", "", labelFont, valueFont);
        addLabeledRow(document, "Djelatnici", "", labelFont, valueFont);
        addLabeledRow(document, "Ugovor", nalog.getUgovor(), labelFont, valueFont);
        addLabeledRow(document, "Broj ponude", nalog.getBrojPonude(), labelFont, valueFont);
        addLabeledRow(document, "Narudžbenica", nalog.getNarudzbenica(), labelFont, valueFont);
        addLabeledRow(document, "Broj računa", nalog.getBrojRacuna(), labelFont, valueFont);
        addLabeledRow(document, "Napomena", narucitelj != null ? narucitelj.getComment() : "", labelFont, valueFont);

        document.close();
        return baos.toByteArray();
    }

    private void addLabeledRow(Document document, String label, String value, Font labelFont, Font valueFont) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{35, 65});
        } catch (DocumentException e) {
            // ignore
        }

        PdfPCell labelCell = new PdfPCell(new Phrase(label + ":", labelFont));
        labelCell.setBorder(0);
        labelCell.setPaddingBottom(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
        valueCell.setBorder(0);
        valueCell.setPaddingBottom(4);
        table.addCell(valueCell);

        table.setSpacingAfter(2);
        document.add(table);
    }

    private List<String> resolveAktivnosti(String aktivnostiJson) {
        if (aktivnostiJson == null || aktivnostiJson.isBlank()) {
            return new ArrayList<>();
        }

        try {
            List<Object> parsed = objectMapper.readValue(aktivnostiJson, new TypeReference<List<Object>>() {});
            List<Long> numericIds = new ArrayList<>();
            List<String> stringValues = new ArrayList<>();

            for (Object item : parsed) {
                if (item instanceof Number) {
                    numericIds.add(((Number) item).longValue());
                } else if (item instanceof String) {
                    String s = ((String) item).trim();
                    if (s.matches("^\\d+$")) {
                        numericIds.add(Long.parseLong(s));
                    } else if (!s.isEmpty()) {
                        stringValues.add(s);
                    }
                }
            }

            if (!numericIds.isEmpty()) {
                List<Aktivnost> configs = aktivnostRepository.findByIdInAndIsActiveTrue(numericIds);
                Map<Long, String> configById = configs.stream()
                        .collect(Collectors.toMap(Aktivnost::getId, Aktivnost::getAktivnost));

                for (Long id : numericIds) {
                    String name = configById.get(id);
                    if (name != null) {
                        stringValues.add(name);
                    }
                }
            }

            return stringValues;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String buildFileName(RadniNalog nalog) {
        String brojNaloga = nalog.getBrojNaloga() != null ? nalog.getBrojNaloga() : "nalog-" + nalog.getId();
        String safeName = brojNaloga.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        return safeName + ".pdf";
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
    }

    private String joinAddress(Narucitelj narucitelj) {
        if (narucitelj == null) return "";

        List<String> parts = new ArrayList<>();
        if (narucitelj.getAdresa() != null && !narucitelj.getAdresa().isBlank())
            parts.add(narucitelj.getAdresa().trim());
        if (narucitelj.getPostanskiBroj() != null && !narucitelj.getPostanskiBroj().isBlank())
            parts.add(narucitelj.getPostanskiBroj().trim());
        if (narucitelj.getMjesto() != null && !narucitelj.getMjesto().isBlank())
            parts.add(narucitelj.getMjesto().trim());
        if (narucitelj.getDrzava() != null && !narucitelj.getDrzava().isBlank())
            parts.add(narucitelj.getDrzava().trim());

        return String.join(", ", parts);
    }
}
