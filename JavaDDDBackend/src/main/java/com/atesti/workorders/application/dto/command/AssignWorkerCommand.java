package com.atesti.workorders.application.dto.command;

import lombok.Data;

@Data
public class AssignWorkerCommand {
    private Long assignedUserId;
}
