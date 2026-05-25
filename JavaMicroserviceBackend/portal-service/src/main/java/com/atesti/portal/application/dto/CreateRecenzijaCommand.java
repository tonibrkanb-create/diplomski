package com.atesti.portal.application.dto;

import lombok.Data;

@Data
public class CreateRecenzijaCommand {
    private Long radniNalogId;
    private Integer ocjena;
    private String komentar;
}
