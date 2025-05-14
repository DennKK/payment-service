package org.dk.paymentservice.service.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.error.PaymentTransactionValidationException;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.entity.BankAccount;
import org.dk.paymentservice.model.entity.Refund;
import org.dk.paymentservice.service.BankAccountService;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;

    @Transactional(readOnly = true)
    public void validateCreatePaymentTransactionRequest(CreatePaymentTransactionRequest request) {
        var violations = validator.validate(request);

        List<String> errors =
            new ArrayList<>(violations.stream().map(ConstraintViolation::getMessage).toList());

        Optional<BankAccount> sourceBankAccount = Optional.empty();
        if (request.getSourceBankAccountId() != null) {
            sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId());
            if (sourceBankAccount.isEmpty()) {
                errors.add(
                    "Source bank account does not exist: " + request.getSourceBankAccountId());
            }
        }

        if (request.getDestinationBankAccountId() != null) {
            if (bankAccountService.findById(request.getSourceBankAccountId()).isEmpty()) {
                errors.add(
                    "Destination bank account does not exist: "
                        + request.getDestinationBankAccountId());
            }
        }

        if (request.getAmount() != null && sourceBankAccount.isPresent()) {
            if (sourceBankAccount.get().getBalance().compareTo(request.getAmount()) < 0) {
                errors.add(
                    "Source bank account balance less than requested amount, source account id: "
                        + request.getSourceBankAccountId());
            }
        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

    @Transactional(readOnly = true)
    public void validateCancelPaymentTransactionRequest(CancelPaymentTransactionRequest request) {
        var violations = validator.validate(request);

        List<String> errors =
            new ArrayList<>(violations.stream().map(ConstraintViolation::getMessage).toList());

        if (request.getPaymentTransactionId() != null) {
            var transaction = paymentTransactionService.findByIdWithRefunds(request.getPaymentTransactionId());

            if (transaction.isEmpty()) {
                errors.add(
                    "Payment transaction id not found, payment transaction id: " + request.getPaymentTransactionId());
            } else {
                var existedTransaction = transaction.get();
                var refundedAmount = existedTransaction.getRefunds().stream()
                    .map(Refund::getRefundedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (existedTransaction.getAmount().subtract(refundedAmount).compareTo(request.getRefundedAmount()) <
                    0) {
                    errors.add(
                        "Request amount for refund is bigger than payment transaction amount, payment transaction: " +
                            request.getPaymentTransactionId());
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }
}
