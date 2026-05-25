package com.atesti.portal.application.dto.command;

import lombok.Data;

@Data
public class KorisnikRegisterCommand {
    private String ime;
    private String prezime;
    private String email;
    private String telefon;
    private String tvrtka;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
    private String password;
}
