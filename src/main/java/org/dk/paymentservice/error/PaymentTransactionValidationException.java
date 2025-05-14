package org.dk.paymentservice.error;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentTransactionValidationException extends RuntimeException {
    private final List<String> errorMessages;
}
