package com.atesti.staffidentity.application.dto.command;

import lombok.Data;

@Data
public class RegisterCommand {
    private String username;
    private String password;
}
