package com.atesti.portal.application.dto.command;

import lombok.Data;

@Data
public class SendObavijestCommand {
    private Long korisnikId;
    private String naslov;
    private String poruka;
}
