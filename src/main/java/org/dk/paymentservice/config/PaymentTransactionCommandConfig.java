package org.dk.paymentservice.config;

import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.handler.CancelPaymentTransactionHandler;
import org.dk.paymentservice.service.handler.CreatePaymentTransactionHandler;
import org.dk.paymentservice.service.handler.PaymentTransactionCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentTransactionCommandConfig {

    @Bean
    public Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers(
            final CreatePaymentTransactionHandler createPaymentTransactionHandler,
            final CancelPaymentTransactionHandler cancelPaymentTransactionHandler) {
        return Map.of(
                PaymentTransactionCommand.CREATE, createPaymentTransactionHandler,
                PaymentTransactionCommand.REFUND, cancelPaymentTransactionHandler);
    }
}
