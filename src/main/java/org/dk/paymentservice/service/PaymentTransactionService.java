package org.dk.paymentservice.service;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.entity.Refund;
import org.dk.paymentservice.model.enums.PaymentTransactionStatus;
import org.dk.paymentservice.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BankAccountService bankAccountService;

    public Optional<PaymentTransaction> findByIdWithRefunds(Long id) {
        return paymentTransactionRepository.findById(id);
    }

    public PaymentTransaction getTransactionByIdWithRefunds(Long id) {
        return paymentTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment transaction not found: " + id));
    }

    @Transactional
    public PaymentTransaction createTransaction(CreatePaymentTransactionRequest request) {
        var sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId())
            .orElseThrow(() -> new RuntimeException("Source account not found"));

        var destinationBankAccount = request.getDestinationBankAccountId() != null
            ? bankAccountService.findById(request.getDestinationBankAccountId())
            .orElseThrow(() -> new RuntimeException("Destination account not found"))
            : null;

        var transaction = new PaymentTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setSourceBankAccount(sourceBankAccount);
        transaction.setDestinationBankAccount(destinationBankAccount);
        transaction.setStatus(PaymentTransactionStatus.PROCESSING);

        return paymentTransactionRepository.save(transaction);
    }

    @Transactional
    public PaymentTransaction processTransaction(PaymentTransaction transaction) {
        try {
            bankAccountService.transferFunds(
                transaction.getSourceBankAccount(),
                transaction.getDestinationBankAccount(),
                transaction.getAmount()
            );
            transaction.setStatus(PaymentTransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(PaymentTransactionStatus.FAILED);
            transaction.setErrorMessage(e.getMessage());
        }
        return paymentTransactionRepository.save(transaction);
    }

    public boolean canBeRefunded(PaymentTransaction transaction, BigDecimal refundAmount) {
        var totalRefunded = transaction.getRefunds().stream()
            .map(Refund::getRefundedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return transaction.getAmount().subtract(totalRefunded).compareTo(refundAmount) >= 0;
    }

    @Transactional
    public void processRefund(PaymentTransaction transaction, BigDecimal refundAmount) {
        if (transaction.getDestinationBankAccount() != null) {
            bankAccountService.transferFunds(
                transaction.getDestinationBankAccount(),
                transaction.getSourceBankAccount(),
                refundAmount
            );
        } else {
            bankAccountService.creditAccount(
                transaction.getSourceBankAccount(),
                refundAmount
            );
        }
    }
}
