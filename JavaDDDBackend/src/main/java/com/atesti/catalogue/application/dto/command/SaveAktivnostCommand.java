package com.atesti.catalogue.application.dto.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveAktivnostCommand {
    private String aktivnost;
    private Integer rokTrajanja;
    private BigDecimal cijena;
}
