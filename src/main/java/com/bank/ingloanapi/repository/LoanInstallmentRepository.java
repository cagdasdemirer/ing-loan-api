package com.bank.ingloanapi.repository;

import com.bank.ingloanapi.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanIdOrderByDueDateAsc(Long loanId);
}
