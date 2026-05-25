package com.atesti.workorders.application.dto.command;

import lombok.Data;

@Data
public class AddDocumentCommand {
    private String name;
    private String blob;
    private String url;
}
