package org.dk.paymentservice.service.handler;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.BankAccountService;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.dk.paymentservice.service.RefundService;
import org.dk.paymentservice.service.validator.PaymentTransactionValidator;
import org.dk.paymentservice.util.JsonConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final RefundService refundService;
    private final JsonConverter jsonConverter;
    private final PaymentTransactionService paymentTransactionService;
    private final BankAccountService bankAccountService;

    @Override
    @Transactional
    public void process(String requestId, String requestBody) {
        var request = jsonConverter.toObject(requestBody, CancelPaymentTransactionRequest.class);
        paymentTransactionValidator.validateCancelPaymentTransactionRequest(request);

        var sourceTransaction = paymentTransactionService.findByIdWithRefunds(request.getPaymentTransactionId())
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        updateBalances(sourceTransaction, request.getRefundedAmount());

        var response = refundService.createRefund(request, sourceTransaction);

        paymentTransactionProducer.sendCommandResult(
            requestId,
            jsonConverter.toJson(response),
            PaymentTransactionCommand.REFUND
        );
    }

    private void updateBalances(PaymentTransaction transaction, BigDecimal refundAmount) {
        var sourceBankAccount = transaction.getSourceBankAccount();
        sourceBankAccount.setBalance(sourceBankAccount.getBalance().add(refundAmount));

        if (transaction.getDestinationBankAccount() != null) {
            var destinationBankAccount = transaction.getDestinationBankAccount();
            destinationBankAccount.setBalance(
                destinationBankAccount.getBalance().subtract(refundAmount)
            );
            bankAccountService.saveAll(List.of(sourceBankAccount, destinationBankAccount));
        } else {
            bankAccountService.saveAll(List.of(sourceBankAccount));
        }
    }
}
