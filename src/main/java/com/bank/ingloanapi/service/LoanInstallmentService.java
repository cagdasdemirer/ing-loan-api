package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.LoanInstallment;
import com.bank.ingloanapi.repository.LoanInstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanInstallmentService {

    @Autowired
    private LoanInstallmentRepository installmentRepository;

    public LoanInstallment saveInstallment(LoanInstallment installment) {
        return installmentRepository.save(installment);
    }

    public List<LoanInstallment> findInstallmentsByLoanId(Long loanId) {
        return installmentRepository.findByLoanIdOrderByDueDateAsc(loanId);
    }

    public boolean canInstallmentBePaid(LoanInstallment installment, LocalDate paymentDate) {
        return !installment.getDueDate().isBefore(paymentDate.minusMonths(3)) && !installment.getIsPaid();
    }

    public void markInstallmentAsPaid(LoanInstallment installment, BigDecimal amountPaid, LocalDate paymentDate) {
        installment.setPaidAmount(amountPaid);
        installment.setPaymentDate(paymentDate);
        installment.setIsPaid(true);
        installmentRepository.save(installment);
    }

    public LoanInstallment payInstallment(Long installmentId, BigDecimal amountPaid) {
        LoanInstallment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new IllegalArgumentException("Installment not found for ID: " + installmentId));

        LocalDate currentDate = LocalDate.now();
        if (!canInstallmentBePaid(installment, currentDate)) {
            throw new IllegalStateException("Installment cannot be paid. Either overdue by 3 months or already paid.");
        }

        if (amountPaid.compareTo(installment.getAmount()) < 0) {
            throw new IllegalArgumentException("Paid amount cannot be less than the installment amount.");
        }

        installment.setPaidAmount(amountPaid);
        installment.setPaymentDate(currentDate);
        installment.setIsPaid(true);

        return installmentRepository.save(installment);
    }
}
