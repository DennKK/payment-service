package org.dk.paymentservice.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dk.paymentservice.model.enums.PaymentTransactionCommand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    public static final String RESULT_TOPIC = "payment-command-result";
    private static final String COMMAND_TOPIC = "payment-command";
    private static final String PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER = "command";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCommand(String requestId, String message, PaymentTransactionCommand command) {
        var kafkaMessage = buildMessage(COMMAND_TOPIC, requestId, message, command.name());
        kafkaTemplate.send(kafkaMessage);
        log.info("Successfully sent Kafka command message={}", kafkaMessage);
    }

    public void sendCommandResult(String requestId, String message, PaymentTransactionCommand command) {
        var kafkaMessage = buildMessage(RESULT_TOPIC, requestId, message, command.name());
        kafkaTemplate.send(kafkaMessage);
        log.info("Successfully sent Kafka result message={}", kafkaMessage);
    }

    private Message<String> buildMessage(
        String topic, String requestId, String message, String command
    ) {
        return MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader(KafkaHeaders.KEY, requestId)
            .setHeader(PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER, command)
            .build();
    }
}
