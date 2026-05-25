package com.atesti.identity.application.dto;

import lombok.Data;

@Data
public class LoginCommand {
    private String username;
    private String password;
}
