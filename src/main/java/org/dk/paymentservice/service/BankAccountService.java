package org.dk.paymentservice.service;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.model.entity.BankAccount;
import org.dk.paymentservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id);
    }

    public boolean hasSufficientFunds(BankAccount account, BigDecimal amount) {
        return account.getBalance().compareTo(amount) >= 0;
    }

    @Transactional
    public void debitAccount(BankAccount account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        bankAccountRepository.save(account);
    }

    @Transactional
    public void creditAccount(BankAccount account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        bankAccountRepository.save(account);
    }

    @Transactional
    public void transferFunds(BankAccount source, BankAccount destination, BigDecimal amount) {
        debitAccount(source, amount);
        if (destination != null) {
            creditAccount(destination, amount);
        }
    }
}
