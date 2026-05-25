package com.atesti.staffidentity.application.dto.command;

import lombok.Data;

@Data
public class CreateUserCommand {
    private String username;
    private String password;
    private String ime;
    private String prezime;
    private String email;
}
