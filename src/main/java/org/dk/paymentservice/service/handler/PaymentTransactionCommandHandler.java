package org.dk.paymentservice.service.handler;

public interface PaymentTransactionCommandHandler {
    void process(String requestId, String requestBody);
}
