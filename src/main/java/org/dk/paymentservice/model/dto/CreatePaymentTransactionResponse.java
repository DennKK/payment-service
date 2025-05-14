package org.dk.paymentservice.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dk.paymentservice.model.dto.enums.CommandResultStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionResponse {
    private Long paymentTransactionId;
    private CommandResultStatus status;
    private String errorMessage;
    private LocalDateTime executedAt;
}
