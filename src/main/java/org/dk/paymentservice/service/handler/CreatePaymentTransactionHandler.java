package org.dk.paymentservice.service.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.BankAccountService;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.dk.paymentservice.service.validator.PaymentTransactionValidator;
import org.dk.paymentservice.util.JsonConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final BankAccountService bankAccountService;
    private final JsonConverter jsonConverter;

    @Override
    @Transactional
    public void process(String requestId, String message) {
        var request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);
        paymentTransactionValidator.validateCreatePaymentTransactionRequest(request);

        var transaction = paymentTransactionService.createTransaction(request);
        updateBalances(transaction);

        var response = paymentTransactionService.processTransaction(transaction);

        paymentTransactionProducer.sendCommandResult(
            requestId,
            jsonConverter.toJson(response),
            PaymentTransactionCommand.CREATE
        );
    }

    private void updateBalances(PaymentTransaction transaction) {
        var sourceBankAccount = transaction.getSourceBankAccount();
        sourceBankAccount.setBalance(sourceBankAccount.getBalance().subtract(transaction.getAmount()));

        if (transaction.getDestinationBankAccount() != null) {
            var destinationBankAccount = transaction.getDestinationBankAccount();
            destinationBankAccount.setBalance(destinationBankAccount.getBalance().add(transaction.getAmount()));
            bankAccountService.saveAll(List.of(sourceBankAccount, destinationBankAccount));
        } else {
            bankAccountService.saveAll(List.of(sourceBankAccount));
        }
    }
}
