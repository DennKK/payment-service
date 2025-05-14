package org.dk.paymentservice.repository;

import java.util.Optional;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    @Query("SELECT pt FROM PaymentTransaction pt LEFT JOIN FETCH pt.refunds WHERE pt.id = :id")
    Optional<PaymentTransaction> findByIdWithRefunds(@Param("id") Long id);
}
