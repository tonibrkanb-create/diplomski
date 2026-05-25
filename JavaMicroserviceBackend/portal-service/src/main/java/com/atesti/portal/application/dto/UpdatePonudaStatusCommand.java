package com.atesti.portal.application.dto;

import lombok.Data;

@Data
public class UpdatePonudaStatusCommand {
    private String status;
    private String odgovor;
}
