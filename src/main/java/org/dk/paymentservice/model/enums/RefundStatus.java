package org.dk.paymentservice.model.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {
    COMPLETED, FAILED;

    public static RefundStatus fromString(String status) {
        for (var refundStatus : RefundStatus.values()) {
            if (refundStatus.toString().equalsIgnoreCase(status)) {
                return refundStatus;
            }
        }

        throw new IllegalArgumentException("Unknown RefundStatus: " + status);
    }
}
