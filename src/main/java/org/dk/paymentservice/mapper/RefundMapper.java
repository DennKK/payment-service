package org.dk.paymentservice.mapper;

import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionResponse;
import org.dk.paymentservice.model.dto.enums.CommandResultStatus;
import org.dk.paymentservice.model.entity.Refund;
import org.dk.paymentservice.model.enums.RefundStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    Refund toEntity(CancelPaymentTransactionRequest request);

    @Mapping(target = "refundId", source = "id")
    @Mapping(target = "status", expression = "java(convertStatus(refund.getStatus()))")
    CancelPaymentTransactionResponse toResponse(Refund refund);

    default CommandResultStatus convertStatus(RefundStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case COMPLETED -> CommandResultStatus.SUCCESS;
            case FAILED -> CommandResultStatus.FAILED;
        };
    }
}
