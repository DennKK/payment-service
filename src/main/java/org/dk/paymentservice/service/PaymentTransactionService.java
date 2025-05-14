package org.dk.paymentservice.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.mapper.PaymentTransactionMapper;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CreatePaymentTransactionResponse;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.enums.PaymentTransactionStatus;
import org.dk.paymentservice.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BankAccountService bankAccountService;

    public Optional<PaymentTransaction> findByIdWithRefunds(Long id) {
        return paymentTransactionRepository.findByIdWithRefunds(id);
    }

    @Transactional
    public PaymentTransaction createTransaction(CreatePaymentTransactionRequest request) {
        var transaction = paymentTransactionMapper.toEntity(request);

        var sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId())
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        transaction.setSourceBankAccount(sourceBankAccount);

        if (request.getDestinationBankAccountId() != null) {
            var destinationBankAccount = bankAccountService.findById(request.getDestinationBankAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));
            transaction.setDestinationBankAccount(destinationBankAccount);
        }

        transaction.setStatus(PaymentTransactionStatus.PROCESSING);
        return paymentTransactionRepository.save(transaction);
    }

    @Transactional
    public CreatePaymentTransactionResponse processTransaction(PaymentTransaction transaction) {
        transaction.setStatus(PaymentTransactionStatus.SUCCESS);
        var savedTransaction = paymentTransactionRepository.save(transaction);
        return paymentTransactionMapper.toResponse(savedTransaction);
    }
}
