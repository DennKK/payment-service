package org.dk.paymentservice.mapper;

import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionResponse;
import org.dk.paymentservice.model.dto.enums.CommandResultStatus;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.enums.PaymentTransactionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransaction toEntity(CreatePaymentTransactionRequest request);

    @Mapping(target = "paymentTransactionId", source = "id")
    @Mapping(target = "status", expression = "java(convertStatus(transaction.getStatus()))")
    CreatePaymentTransactionResponse toResponse(PaymentTransaction transaction);

    default CommandResultStatus convertStatus(PaymentTransactionStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PROCESSING -> CommandResultStatus.PROCESSING;
            case SUCCESS -> CommandResultStatus.SUCCESS;
            case FAILED -> CommandResultStatus.FAILED;
        };
    }
}
