package org.dk.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.dk.paymentservice.util.JsonConverter;
import org.dk.paymentservice.validation.transaction.PaymentTransactionBusinessRuleValidator;
import org.dk.paymentservice.validation.transaction.PaymentTransactionRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionRequestValidator paymentTransactionRequestValidator;
    private final PaymentTransactionBusinessRuleValidator paymentTransactionBusinessRuleValidator;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Override
    @Transactional
    public void process(String requestId, String message) {
        var request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);
        paymentTransactionRequestValidator.validateCreatePaymentRequest(request);
        paymentTransactionBusinessRuleValidator.validateCreatePaymentBusinessRules(request);

        var transaction = paymentTransactionService.createTransaction(request);
        var processedTransaction = paymentTransactionService.processTransaction(transaction);

        paymentTransactionProducer.sendCommandResult(
            requestId,
            jsonConverter.toJson(processedTransaction),
            PaymentTransactionCommand.CREATE
        );
    }
}
