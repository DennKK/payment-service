package org.dk.paymentservice.validation.transaction;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.error.PaymentTransactionValidationException;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentTransactionRequestValidator {
    private final Validator validator;

    public void validateCreatePaymentRequest(CreatePaymentTransactionRequest request) {
        Set<ConstraintViolation<CreatePaymentTransactionRequest>> violations = validator.validate(request);
        throwIfViolationsExist(violations);
    }

    public void validateCancelPaymentRequest(CancelPaymentTransactionRequest request) {
        Set<ConstraintViolation<CancelPaymentTransactionRequest>> violations = validator.validate(request);
        throwIfViolationsExist(violations);
    }

    private <T> void throwIfViolationsExist(Set<ConstraintViolation<T>> violations) {
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
            throw new PaymentTransactionValidationException(errors);
        }
    }
}
