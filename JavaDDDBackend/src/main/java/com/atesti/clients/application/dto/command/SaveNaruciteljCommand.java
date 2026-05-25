package com.atesti.clients.application.dto.command;

import lombok.Data;

@Data
public class SaveNaruciteljCommand {
    private String narucitelj;
    private String name;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
    private String OIB;
    private String ziroRacun;
    private String ostalo;
    private String kontaktOsoba;
    private String telefon;
    private String mobitel;
    private String fax;
    private String email;
    private String location;
    private String comment;

    public String resolveName() {
        return narucitelj != null ? narucitelj : name;
    }
}
