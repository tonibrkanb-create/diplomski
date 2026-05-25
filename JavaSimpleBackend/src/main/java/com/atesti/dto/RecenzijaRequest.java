package com.atesti.dto;

import lombok.Data;

@Data
public class RecenzijaRequest {
    private Long radniNalogId;
    private Integer ocjena;
    private String komentar;
}
