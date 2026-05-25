package com.atesti.dto;

import lombok.Data;

@Data
public class SendObavijestRequest {
    private Long korisnikId;
    private String naslov;
    private String poruka;
}
