package org.dk.paymentservice.repository;

import org.dk.paymentservice.model.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
