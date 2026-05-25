package com.atesti.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AktivnostRequest {
    private String aktivnost;
    private Integer rokTrajanja;
    private BigDecimal cijena;
}
