package org.dk.paymentservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionRequest {
    @NotNull(message = "Source bank account ID must not be null")
    private Long sourceBankAccountId;

    private Long destinationBankAccountId;

    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @NotNull(message = "Currency must not be null")
    private String currency;

    private String description;
}
