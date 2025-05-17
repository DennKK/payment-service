package org.dk.paymentservice.validation.transaction;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.error.PaymentTransactionValidationException;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.entity.BankAccount;
import org.dk.paymentservice.service.BankAccountService;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentTransactionBusinessRuleValidator {
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;

    public void validateCreatePaymentBusinessRules(CreatePaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>();

        var sourceAccountOpt = bankAccountService.findById(request.getSourceBankAccountId());
        sourceAccountOpt.ifPresentOrElse(
            sourceAccount -> {
                if (!bankAccountService.hasSufficientFunds(sourceAccount, request.getAmount())) {
                    errors.add("Insufficient funds in source account: " + request.getSourceBankAccountId());
                }
                validateCurrencyMatch(sourceAccount, request.getCurrency(), "Source", errors);
            },
            () -> errors.add("Source account not found: " + request.getSourceBankAccountId())
        );

        if (request.getDestinationBankAccountId() != null) {
            bankAccountService.findById(request.getDestinationBankAccountId())
                .ifPresentOrElse(
                    destinationAccount -> validateCurrencyMatch(
                        destinationAccount,
                        request.getCurrency(),
                        "Destination",
                        errors
                    ),
                    () -> errors.add("Destination account not found: " + request.getDestinationBankAccountId())
                );
        }

        throwIfErrorsExist(errors);
    }

    public void validateCancelPaymentBusinessRules(CancelPaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>();

        paymentTransactionService.findByIdWithRefunds(request.getPaymentTransactionId())
            .ifPresentOrElse(
                transaction -> {
                    if (!paymentTransactionService.canBeRefunded(transaction, request.getRefundedAmount())) {
                        errors.add("Cannot refund amount " + request.getRefundedAmount() +
                            " for transaction " + request.getPaymentTransactionId());
                    }

                    validateCurrencyMatch(
                        transaction.getSourceBankAccount(),
                        transaction.getCurrency(),
                        "Source",
                        errors
                    );

                    if (transaction.getDestinationBankAccount() != null) {
                        validateCurrencyMatch(
                            transaction.getDestinationBankAccount(),
                            transaction.getCurrency(),
                            "Destination",
                            errors
                        );
                    }
                },
                () -> errors.add("Transaction not found: " + request.getPaymentTransactionId())
            );

        throwIfErrorsExist(errors);
    }

    private void validateCurrencyMatch(
        BankAccount account,
        String expectedCurrency,
        String accountType,
        List<String> errors
    ) {
        if (!account.getCurrency().equals(expectedCurrency)) {
            errors.add(accountType + " account currency (" + account.getCurrency() +
                ") does not match transaction currency (" + expectedCurrency + ")");
        }
    }

    private void throwIfErrorsExist(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }
}
