package org.dk.paymentservice.repository;

import org.dk.paymentservice.model.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
