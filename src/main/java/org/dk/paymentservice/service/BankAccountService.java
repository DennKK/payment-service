package org.dk.paymentservice.service;

import java.util.List;
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

    @Transactional
    public void saveAll(List<BankAccount> accounts) {
        bankAccountRepository.saveAll(accounts);
    }

    @Transactional
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }
}
