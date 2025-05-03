package org.dk.paymentservice.model.enums;

import lombok.Getter;

@Getter
public enum PaymentTransactionStatus {
    PROCESSING, SUCCESS, FAILED;

    public static PaymentTransactionStatus fromString(String status) {
        for (var paymentTransactionStatus : PaymentTransactionStatus.values()) {
            if (paymentTransactionStatus.toString().equalsIgnoreCase(status)) {
                return paymentTransactionStatus;
            }
        }

        throw new IllegalArgumentException("Unknown PaymentTransactionStatus: " + status);
    }
}
