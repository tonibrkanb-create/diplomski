package com.atesti.identity.application.dto;

import lombok.Data;

@Data
public class RegisterCommand {
    private String username;
    private String password;
}
