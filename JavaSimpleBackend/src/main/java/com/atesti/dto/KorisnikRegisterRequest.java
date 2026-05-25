package com.atesti.dto;

import lombok.Data;

@Data
public class KorisnikRegisterRequest {
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
