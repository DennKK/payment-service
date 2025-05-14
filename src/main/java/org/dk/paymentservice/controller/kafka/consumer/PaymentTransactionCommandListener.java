package org.dk.paymentservice.controller.kafka.consumer;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.dk.paymentservice.service.handler.PaymentTransactionCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {

    private static final String COMMAND_TOPIC = "payment-command";
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    @KafkaListener(
        topics = COMMAND_TOPIC,
        containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentTransactionCommand(ConsumerRecord<String, String> record) {
        var command = getPaymentTransactionCommand(record);

        var commandHandler = commandHandlers.get(command);
        if (commandHandler == null) {
            log.error("Unknown command '{}', record={}", command, record);
            throw new IllegalArgumentException("Unknown command: " + command);
        }
        commandHandler.process(record.key(), record.value());
    }

    private PaymentTransactionCommand getPaymentTransactionCommand(
        ConsumerRecord<String, String> record
    ) {
        var commandHandler = record.headers().lastHeader("command");

        if (commandHandler != null) {
            return PaymentTransactionCommand.fromString(new String(commandHandler.value()));
        }

        return PaymentTransactionCommand.UNKNOWN;
    }
}
