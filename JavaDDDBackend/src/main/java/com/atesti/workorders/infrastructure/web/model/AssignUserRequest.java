package com.atesti.workorders.infrastructure.web.model;

import com.atesti.workorders.application.dto.command.AssignWorkerCommand;
import lombok.Data;

@Data
public class AssignUserRequest {

    private Long assignedUserId;

    public AssignWorkerCommand toCommand() {
        return new AssignWorkerCommand(assignedUserId);
    }
}