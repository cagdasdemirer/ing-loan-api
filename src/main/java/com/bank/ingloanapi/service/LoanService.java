package com.bank.ingloanapi.service;

import com.bank.ingloanapi.model.Loan;
import com.bank.ingloanapi.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public Optional<Loan> findLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> findLoansByCustomerId(Long customerId, Boolean isPaid, Integer numberOfInstallments) {
        List<Loan> loans = loanRepository.findByCustomerId(customerId);

        if (isPaid != null) {
            loans = loans.stream()
                    .filter(loan -> loan.getIsPaid().equals(isPaid))
                    .collect(Collectors.toList());
        }

        if (numberOfInstallments != null) {
            loans = loans.stream()
                    .filter(loan -> loan.getNumberOfInstallments().equals(numberOfInstallments))
                    .collect(Collectors.toList());
        }

        return loans;
    }

    public void markLoanAsPaid(Loan loan) {
        loan.setIsPaid(true);
        loanRepository.save(loan);
    }
}
