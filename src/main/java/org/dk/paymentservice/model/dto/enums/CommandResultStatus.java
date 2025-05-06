package org.dk.paymentservice.model.dto.enums;

import lombok.Getter;

@Getter
public enum CommandResultStatus {
    SUCCESS, FAILED;

    public static CommandResultStatus fromString(String status) {
        for (var commandResultStatus : CommandResultStatus.values()) {
            if (commandResultStatus.toString().equalsIgnoreCase(status)) {
                return commandResultStatus;
            }
        }

        throw new IllegalArgumentException("Unknown CommandResultStatus: " + status);
    }
}
