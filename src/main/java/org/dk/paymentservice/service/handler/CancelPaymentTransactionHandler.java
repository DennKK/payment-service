package org.dk.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.PaymentTransactionService;
import org.dk.paymentservice.service.RefundService;
import org.dk.paymentservice.util.JsonConverter;
import org.dk.paymentservice.validation.transaction.PaymentTransactionBusinessRuleValidator;
import org.dk.paymentservice.validation.transaction.PaymentTransactionRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final PaymentTransactionService paymentTransactionService;
    private final RefundService refundService;
    private final PaymentTransactionRequestValidator paymentTransactionRequestValidator;
    private final PaymentTransactionBusinessRuleValidator paymentTransactionBusinessRuleValidator;
    private final JsonConverter jsonConverter;

    @Override
    @Transactional
    public void process(String requestId, String requestBody) {
        var request = jsonConverter.toObject(requestBody, CancelPaymentTransactionRequest.class);
        paymentTransactionRequestValidator.validateCancelPaymentRequest(request);
        paymentTransactionBusinessRuleValidator.validateCancelPaymentBusinessRules(request);

        var transaction = paymentTransactionService.getTransactionByIdWithRefunds(request.getPaymentTransactionId());
        paymentTransactionService.processRefund(transaction, request.getRefundedAmount());

        var response = refundService.createRefund(request, transaction);
        paymentTransactionProducer.sendCommandResult(
            requestId,
            jsonConverter.toJson(response),
            PaymentTransactionCommand.REFUND
        );
    }
}
