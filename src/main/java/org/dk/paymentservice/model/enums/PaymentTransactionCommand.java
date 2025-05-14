package org.dk.paymentservice.model.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum PaymentTransactionCommand {
    CREATE,
    REFUND,
    UNKNOWN;

    public static PaymentTransactionCommand fromString(String status) {
        try {
            return PaymentTransactionCommand.valueOf(status);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return UNKNOWN;
        }
    }
}
