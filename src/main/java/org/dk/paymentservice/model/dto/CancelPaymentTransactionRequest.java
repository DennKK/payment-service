package org.dk.paymentservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentTransactionRequest {
    @NotNull(message = "Payment transaction ID must not be null")
    private Long paymentTransactionId;

    @NotNull(message = "Refunded amount must not be null")
    @Min(value = 1, message = "Refunded amount must be at least 1")
    private BigDecimal refundedAmount;

    private String reason;
}
