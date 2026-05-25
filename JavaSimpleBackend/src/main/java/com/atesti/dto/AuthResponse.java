package com.atesti.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Map<String, Object> user;
    private String token;
}
