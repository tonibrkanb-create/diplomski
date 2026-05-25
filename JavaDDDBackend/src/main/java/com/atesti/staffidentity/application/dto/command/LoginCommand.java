package com.atesti.staffidentity.application.dto.command;

import lombok.Data;

@Data
public class LoginCommand {
    private String username;
    private String password;
}
