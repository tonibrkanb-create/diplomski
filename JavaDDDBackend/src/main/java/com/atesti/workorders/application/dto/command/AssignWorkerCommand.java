package com.atesti.workorders.application.dto.command;

public record AssignWorkerCommand(
        Long assignedUserId
) {

    public AssignWorkerCommand {
        if (assignedUserId == null) {
            throw new IllegalArgumentException("Assigned user id is required");
        }
    }
}