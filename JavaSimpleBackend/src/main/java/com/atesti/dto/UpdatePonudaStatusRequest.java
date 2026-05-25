package com.atesti.dto;

import lombok.Data;

@Data
public class UpdatePonudaStatusRequest {
    private String status;
    private String odgovor;
}
