package com.bank.ingloanapi.controller;

import com.bank.ingloanapi.model.Loan;
import com.bank.ingloanapi.model.LoanInstallment;
import com.bank.ingloanapi.service.LoanInstallmentService;
import com.bank.ingloanapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/installments")
public class InstallmentController {

    @Autowired
    private LoanInstallmentService installmentService;

    @Autowired
    private LoanService loanService;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanInstallment>> listInstallments(@PathVariable Long loanId) {
        List<LoanInstallment> installments = installmentService.findInstallmentsByLoanId(loanId);
        return ResponseEntity.ok(installments);
    }

    @PostMapping("/pay")
    public ResponseEntity<String> payInstallments(
            @RequestParam Long loanId,
            @RequestParam double paymentAmount) {

        List<LoanInstallment> installments = installmentService.findInstallmentsByLoanId(loanId);
        double remainingAmount = paymentAmount;

        int installmentsPaid = 0;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (LoanInstallment installment : installments) {
            if (remainingAmount < installment.getAmount().doubleValue()) {
                break;
            }

            if (!installmentService.canInstallmentBePaid(installment, LocalDate.now())) {
                continue;
            }

            long daysDifference = ChronoUnit.DAYS.between(installment.getDueDate(), LocalDate.now());

            BigDecimal adjustment = BigDecimal.ZERO;

            if (daysDifference < 0) {
                // Paid before due date: Apply a discount
                adjustment = BigDecimal.valueOf(Math.abs(daysDifference) * 0.001);
                installment.setPaidAmount(installment.getAmount().subtract(adjustment));
            } else if (daysDifference > 0) {
                // Paid after due date: Apply a penalty
                adjustment = BigDecimal.valueOf(daysDifference * 0.001);
                installment.setPaidAmount(installment.getAmount().add(adjustment));
            } else {
                // Paid on time: No adjustment needed
                installment.setPaidAmount(installment.getAmount());
            }

            // Mark installment as paid with the adjusted amount
            installmentService.markInstallmentAsPaid(installment, installment.getPaidAmount(), LocalDate.now());

            remainingAmount -= installment.getPaidAmount().doubleValue();
            installmentsPaid++;
            totalPaid = totalPaid.add(installment.getPaidAmount());
        }

        if (installmentsPaid == 0) {
            return ResponseEntity.badRequest().body("Insufficient payment or no eligible installments to pay.");
        }

        boolean isLoanFullyPaid = loanService.findLoanById(loanId)
                .map(Loan::getIsPaid)
                .orElse(false);

        if (isLoanFullyPaid) {
            loanService.findLoanById(loanId).ifPresent(loanService::markLoanAsPaid);
        }

        return ResponseEntity.ok(String.format("Paid %d installments. Total paid: %.2f", installmentsPaid, totalPaid.doubleValue()));
    }
}
