package org.dk.paymentservice.model.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount extends BaseEntity {
    private Long customerId;
    private String number;
    private BigDecimal balance;
    private String currency;
}
