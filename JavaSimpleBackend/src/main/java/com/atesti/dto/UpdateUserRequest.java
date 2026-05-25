package com.atesti.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String password;
    private String ime;
    private String prezime;
    private String email;
}
