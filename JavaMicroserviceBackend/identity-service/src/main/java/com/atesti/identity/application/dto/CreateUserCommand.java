package com.atesti.identity.application.dto;

import lombok.Data;

@Data
public class CreateUserCommand {
    private String username;
    private String password;
    private String ime;
    private String prezime;
    private String email;
}
