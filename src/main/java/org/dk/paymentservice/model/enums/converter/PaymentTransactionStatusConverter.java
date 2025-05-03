package org.dk.paymentservice.model.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.dk.paymentservice.model.enums.PaymentTransactionStatus;

@Converter(autoApply = true)
public class PaymentTransactionStatusConverter implements AttributeConverter<PaymentTransactionStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentTransactionStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public PaymentTransactionStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PaymentTransactionStatus.fromString(dbData);
    }
}
