package com.atesti.staffidentity.application.dto.command;

import lombok.Data;

@Data
public class UpdateUserCommand {
    private String username;
    private String password;
    private String ime;
    private String prezime;
    private String email;
}
