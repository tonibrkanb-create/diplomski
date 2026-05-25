package com.atesti.workorders.application.dto.command;

import lombok.Data;

@Data
public class AddNoteCommand {
    private String date;
    private String text;
}
