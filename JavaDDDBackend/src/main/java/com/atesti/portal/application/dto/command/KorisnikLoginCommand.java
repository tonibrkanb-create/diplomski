package com.atesti.portal.application.dto.command;

import lombok.Data;

@Data
public class KorisnikLoginCommand {
    private String email;
    private String password;
}
