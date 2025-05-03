package org.dk.paymentservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dk.paymentservice.model.enums.PaymentTransactionStatus;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends BaseEntity {
    private BigDecimal amount;
    private String currency;
    private PaymentTransactionStatus status;
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "sourceBankAccountId", referencedColumnName = "id")
    private BankAccount sourceBankAccount;

    @ManyToOne
    @JoinColumn(name = "destinationAccountId", referencedColumnName = "id")
    private BankAccount destinationAccount;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Refund> refunds;
}
