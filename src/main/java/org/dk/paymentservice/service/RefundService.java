package org.dk.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.dk.paymentservice.mapper.RefundMapper;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionRequest;
import org.dk.paymentservice.model.dto.CancelPaymentTransactionResponse;
import org.dk.paymentservice.model.entity.PaymentTransaction;
import org.dk.paymentservice.model.enums.RefundStatus;
import org.dk.paymentservice.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundMapper refundMapper;
    private final RefundRepository refundRepository;

    @Transactional
    public CancelPaymentTransactionResponse createRefund(
        CancelPaymentTransactionRequest request,
        PaymentTransaction paymentTransaction
    ) {
        var refund = refundMapper.toEntity(request);
        refund.setStatus(RefundStatus.COMPLETED);
        refund.setPaymentTransaction(paymentTransaction);

        var savedRefund = refundRepository.save(refund);
        return refundMapper.toResponse(savedRefund);
    }
}
